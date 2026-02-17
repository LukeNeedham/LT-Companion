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
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState.InProgress.PlayingState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.player.AudioPlayer
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
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
    private val soundEffectPlayer: SoundEffectPlayer,
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

    /** The points at which the pausepoint trigger should fire */
    private val triggerPausepoints: List<Millis>
        get() = pausepoints.map { (it - pausepointTriggerOffset).coerceAtLeast(0) }

    /**
     * The pausepoints which have been handled by auto-pausing them.
     * Each pausepoint gets handled exactly once,
     * so if the user skips back in time the pausepoint is not re-triggered.
     */
    private val handledPausepoints = mutableListOf<Millis>()

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
                // Reset handled pausepoints when pausepoints change
                handledPausepoints.clear()
                refreshUiState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
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
                    is PlayingState.Playing -> pausePlayback(PlayingState.Paused.Reason.Manual)
                    is PlayingState.Paused -> resumePlayback()
                }
            }
        }
    }

    /**
     * Skips backward by 5 seconds.
     * Also resets the lastProcessedPausePointIndex to allow pausing at pausepoints we've already passed.
     */
    fun skipBackward() {
        // We may need to retrigger pausepoints
        handledPausepoints.clear()
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
    private fun pausePlayback(reason: PlayingState.Paused.Reason) {
        audioPlayer.pause()

        playingState = PlayingState.Paused(reason)
        refreshUiState()
    }

    private fun getPausepointFractions(): List<Float> {
        val duration = audioPlayer.duration
        if (duration <= 0) return emptyList()
        return triggerPausepoints.map { pausePoint ->
            pausePoint.toFloat() / duration.toFloat()
        }
    }

    /**
     * Resumes the paused playback.
     * Also updates the lastProcessedPausePointIndex to handle pausepoints correctly when resuming.
     */
    private fun resumePlayback() {
        audioPlayer.play()

        playingState = PlayingState.Playing
        refreshUiState()
    }

    /**
     * Updates the UI state with the current playback position.
     * Also checks if the current position matches any pausepoint and pauses playback if needed.
     */
    private fun updatePlaybackPosition(currentPosition: Millis) {
        // Check if we need to pause at a pausepoint
        if (audioPlayer.isPlaying) {
            checkPausepoints(currentPosition)
        }

        refreshUiState()
    }

    private fun getCurrentPlaybackPosition(): Millis {
        return audioPlayer.currentPosition
    }

    /**
     * Checks if the current position matches any pausepoint and pauses playback if needed.
     *
     * @param currentPosition The current playback position in milliseconds
     */
    private fun checkPausepoints(currentPosition: Millis) {
        val nextPausepoint = triggerPausepoints.firstOrNull { pausepoint ->
            pausepoint > currentPosition
        } ?: return

        val millisUntilPausepoint = nextPausepoint - currentPosition

        /**
         * If diff is bigger than progress tick millis then we shouldn't pause yet -
         * we should pause in a future next tick.
         * That said, due to the imperfect nature of ticking and progress rounding,
         * we also don't want to risk missing any pausepoints.
         * For that reason we include the next tick too, in case it would somehow be missed.
         */
        val maxMillisDelta = progressTickMillis * 2
        if (millisUntilPausepoint > maxMillisDelta) return

        // Pausepoint has already been handled - nothing to do
        if (nextPausepoint in handledPausepoints) return

        handledPausepoints.add(nextPausepoint)

        pausePlayback(PlayingState.Paused.Reason.Auto)
        // Just a soft thump
        soundEffectPlayer.play(SoundEffect.Thump, volume = 0.1f)
    }

    private fun getCurrentPlaybackSpeed() = possibleSpeeds[currentSpeedIndex]

    private fun calculateUiState(): LessonState {
        if (isCompleted) return LessonState.Completed

        val e = error
        if (e != null) return LessonState.Error(e)

        val playingState = playingState ?: return LessonState.Loading

        return LessonState.InProgress(
            currentPosition = audioPlayer.currentPosition,
            totalDuration = audioPlayer.duration,
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
            override fun onReady(playWhenReady: Boolean) {
                if (playWhenReady) {
                    playingState = PlayingState.Playing
                    refreshUiState()
                }
            }

            override fun onEnded() {
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
        }
        return audioPlayerProvider.create(uri, callbacks)
    }

    private companion object {
        const val progressTickMillis: Millis = 10

        /** Wait a sec after the end of the audio before moving onto the completed state */
        const val completedDelay: Millis = 500

        /**
         * Offset pointpoints a little bit so they trigger in the player
         * just before the speaking starts
         */
        const val pausepointTriggerOffset: Millis = 0 // 300
    }
}
