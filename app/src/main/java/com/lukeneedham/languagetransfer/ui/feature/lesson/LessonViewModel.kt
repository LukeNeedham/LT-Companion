package com.lukeneedham.languagetransfer.ui.feature.lesson

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.domain.pausepointreport.PausepointReport
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.player.AudioPlayer
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * ViewModel for the Lesson screen that handles audio playback and state management.
 */
class LessonViewModel(
    private val lesson: CourseLesson,
    private val completedLessonRepository: CompletedLessonRepository,
    private val debugOptions: DebugOptions,
    private val lessonPausepointProviderFactory: LessonPausepointProvider.Factory,
    private val audioPlayerProvider: AudioPlayerProvider,
) : ViewModel() {
    private val possibleSpeeds = listOf(1.0f, 1.7f)

    private val lessonPausepointProvider = lessonPausepointProviderFactory.build(lesson)

    private val audioPlayer = createAudioPlayer()

    // Using AudioPlayer abstraction instead of media3 Player
    // The instance is provided via DI as 'audioPlayer' and reused across calls

    /** Actual pausepoint values */
    private var pausepoints: List<Millis> = emptyList()
    private var currentSpeedIndex: Int = 0
    private var showDebugLessonControls: Boolean = false
    private var playingState: PlayingState? = null
    private var isCompleted: Boolean = false
    private var error: String? = null

    var uiState by mutableStateOf<LessonState>(LessonState.Loading)
        private set

    val pausepointReporter = object : PausepointReporter {
        override fun add() {
            this@LessonViewModel.reportPausepointMissing()
        }

        override fun remove() {
            this@LessonViewModel.reportPausepointUnnecessary()
        }

        override fun shiftLater() {
            this@LessonViewModel.reportPausepointTooEarly()
        }

        override fun shiftEarlier() {
            this@LessonViewModel.reportPausepointTooLate()
        }
    }

    init {
        viewModelScope.launch {
            debugOptions.showDebugLessonControls.collect {
                showDebugLessonControls = it
            }
        }
        viewModelScope.launch {
            lessonPausepointProvider.pausepoints.collect { pps ->
                pausepoints = pps
                refreshUiState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }

    /**
     * Toggles between play and pause states.
     */
    fun togglePlayPause() {
        val currentState = uiState
        when (currentState) {
            LessonState.Completed,
            is LessonState.Error,
            LessonState.Loading -> {
                // Ignore
            }

            is LessonState.InProgress -> {
                when (currentState.playingState) {
                    is PlayingState.Playing -> pausePlayback()
                    is PlayingState.Paused -> resumePlayback()
                }
            }
        }
    }

    /**
     * Skips backward by 5 seconds.
     * Also resets the last processed pausepoint index to allow pausing at pausepoints we've already passed.
     */
    fun skipBackward() {
        val currentPosition = audioPlayer.currentPosition
        val newPosition = (currentPosition - 5000).coerceAtLeast(0) // 5 seconds
        audioPlayer.seekTo(newPosition)
        refreshUiState()
    }

    fun togglePlaybackSpeed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        currentSpeedIndex = (currentSpeedIndex + 1) % possibleSpeeds.size
        val playbackSpeed = possibleSpeeds[currentSpeedIndex]
        audioPlayer.setPlaybackSpeed(playbackSpeed)
        refreshUiState()
    }

    fun skipToEnd() {
        val nearEndPosition = audioPlayer.duration - 5000 // 5 seconds from end
        audioPlayer.seekTo(nearEndPosition)
    }

    fun jumpForward() {
        val currentPosition = audioPlayer.currentPosition
        val newPosition = (currentPosition + 10000).coerceAtMost(audioPlayer.duration)
        audioPlayer.seekTo(newPosition)
        refreshUiState()
    }

    fun reportPausepointMissing() {
        val position = getCurrentPlaybackPosition()
        lessonPausepointProvider.report(
            PausepointReport.Add(position)
        )
    }

    fun reportPausepointUnnecessary() {
        val pausepoint = getClosestPausepoint() ?: return
        lessonPausepointProvider.report(
            PausepointReport.Remove(pausepoint)
        )
    }

    fun reportPausepointTooEarly() {
        val pausepoint = getClosestPausepoint() ?: return
        lessonPausepointProvider.report(
            PausepointReport.TooEarly(pausepoint)
        )
    }

    fun reportPausepointTooLate() {
        val pausepoint = getClosestPausepoint() ?: return
        lessonPausepointProvider.report(
            PausepointReport.TooLate(pausepoint)
        )
    }

    /**
     * Pauses the current playback.
     */
    private fun pausePlayback() {
        audioPlayer.pause()
    }

    private fun getPausepointFractions(): List<Float> {
        val duration = audioPlayer.duration
        if (duration <= 0) return emptyList()
        return pausepoints.map { pausePoint ->
            pausePoint.toFloat() / duration.toFloat()
        }
    }

    /**
     * Resumes the paused playback.
     */
    private fun resumePlayback() {
        audioPlayer.play()
    }

    /**
     * Updates the UI state with the current playback position.
     */
    private fun updatePlaybackPosition(currentPosition: Millis) {
        refreshUiState()
    }

    private fun getCurrentPlaybackPosition(): Millis {
        return audioPlayer.currentPosition
    }

    private fun getCurrentPlaybackSpeed() = possibleSpeeds[currentSpeedIndex]

    private fun calculateUiState(): LessonState {
        if (isCompleted) return LessonState.Completed

        val e = error
        if (e != null) return LessonState.Error(e)

        val playingState = playingState ?: return LessonState.Loading

        val duration = audioPlayer.duration
        if (duration == 0L) return LessonState.Loading

        return LessonState.InProgress(
            currentPosition = audioPlayer.currentPosition,
            totalDuration = duration,
            playingState = playingState,
            pausepointFractions = getPausepointFractions(),
            playbackSpeed = getCurrentPlaybackSpeed(),
            showDebugLessonControls = showDebugLessonControls,
        )
    }

    private fun refreshUiState() {
        val newState = calculateUiState()
        uiState = newState
    }

    private fun getClosestPausepoint(): Millis? {
        val position = getCurrentPlaybackPosition()
        return pausepoints.minByOrNull { (it - position).absoluteValue }
    }

    private fun createAudioPlayer(): AudioPlayer {
        val uri = lesson.audioFile.toUri()
        val callbacks = object : AudioPlayer.Callbacks {
            override fun onComplete() {
                viewModelScope.launch {
                    delay(completedDelay)
                    completedLessonRepository.markLessonAsCompleted(lesson.lessonNumber)
                    isCompleted = true
                    refreshUiState()
                }
            }

            override fun onError(message: String) {
                error = "Error playing audio: $message"
                refreshUiState()
            }

            override fun onProgressUpdate(position: Millis) {
                updatePlaybackPosition(position)
            }

            override fun onPlayingStateChange(state: PlayingState?) {
                playingState = state
                refreshUiState()
            }
        }
        return audioPlayerProvider.create(uri, lesson.lessonNumber, callbacks)
    }

    private companion object {
        /** Wait a sec after the end of the audio before moving onto the completed state */
        const val completedDelay: Millis = 500
    }
}
