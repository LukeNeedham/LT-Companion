package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.core.net.toUri
import com.lukeneedham.languagetransfer.data.persistence.prefs.LessonProgressDao
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProviderCache
import com.lukeneedham.languagetransfer.domain.pausepointreport.PausepointReport
import com.lukeneedham.languagetransfer.ui.player.AudioPlayer
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.ui.player.SkipBackward
import com.lukeneedham.languagetransfer.ui.util.sfx.AppSoundEffectPlayer
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class LessonSpecificViewModel(
    val lesson: CourseLesson,
    val coroutineScope: CoroutineScope,
    private val completedLessonRepository: CompletedLessonRepository,
    private val lessonPausepointProviderCache: LessonPausepointProviderCache,
    private val audioPlayerProvider: AudioPlayerProvider,
    private val audioLessonRepository: AudioLessonRepository,
    private val soundEffectPlayer: AppSoundEffectPlayer,
    private val debugOptions: DebugOptions,
    private val lessonProgressDao: LessonProgressDao,
) {
    private val possibleSpeeds = listOf(1.0f, 1.5f, 2.0f)

    private val lessonPausepointProvider = lessonPausepointProviderCache.get(lesson)

    private val audioPlayer = createAudioPlayer()

    val nextLesson: CourseLesson? = calculateNextLesson()

    /** Actual pausepoint values */
    private var pausepoints: List<Millis> = emptyList()
    private var currentSpeedIndex: Int = 0
    private var playingState: PlayingState? = null
    private var isCompleted: Boolean = false
    private var error: String? = null

    private val uiStateMutable = MutableStateFlow<LessonState>(LessonState.Loading)
    val uiState = uiStateMutable.asStateFlow()

    init {
        refreshUiState()

        coroutineScope.launch {
            lessonPausepointProvider.pausepoints.collect { pps ->
                pausepoints = pps
                refreshUiState()
            }
        }

        // Restore saved playback position once the player becomes ready
        coroutineScope.launch {
            val savedPos = lessonProgressDao.getSavedPosition(lesson.lessonNumber) ?: return@launch
            if (savedPos >= minRestoredPositionMs) {
                // Wait until the player finishes loading before seeking
                uiStateMutable.first { it !is LessonState.Loading }
                audioPlayer.seekTo(savedPos)
            }
        }

        // Periodically persist the current playback position for resume-on-return
        coroutineScope.launch {
            while (true) {
                delay(savePositionIntervalMs)
                val pos = audioPlayer.currentPosition
                if (pos > 0 && !isCompleted) {
                    lessonProgressDao.savePosition(lesson.lessonNumber, pos)
                }
            }
        }
    }

    fun dispose() {
        audioPlayer.stop()
        coroutineScope.cancel("ViewModel disposed")
    }

    private fun createAudioPlayer(): AudioPlayer {
        val uri = lesson.audioFile.toUri()
        val callbacks = object : AudioPlayer.Callbacks {
            override fun onComplete() {
                coroutineScope.launch {
                    delay(completedDelay)
                    completedLessonRepository.markLessonAsCompleted(lesson.lessonNumber)
                    // Clear saved position now that the lesson is fully completed
                    lessonProgressDao.clearPosition(lesson.lessonNumber)
                    isCompleted = true
                    // Stop the audio player, remove the media notification
                    audioPlayer.stop()
                    soundEffectPlayer.play(SoundEffect.Completed)
                    refreshUiState()
                }
            }

            override fun onError(message: String) {
                error = "Error playing audio: $message"
                refreshUiState()
            }

            override fun onProgressUpdate(position: Millis) {
                refreshUiState()
            }

            override fun onPlayingStateChange(state: PlayingState?) {
                playingState = state
                refreshUiState()
            }
        }
        return audioPlayerProvider.create(uri, lesson.lessonNumber, callbacks)
    }

    private fun calculateUiState(): LessonState {
        if (isCompleted) {
            return LessonState.Completed(
                hasCompletedCourse = nextLesson == null
            )
        }

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
        )
    }

    private fun getCurrentPlaybackSpeed() = possibleSpeeds[currentSpeedIndex]

    private fun calculateNextLesson(): CourseLesson? {
        val courseRes = audioLessonRepository.getLanguageCourse()
        return when (courseRes) {
            is AppResult.Failure -> null
            is AppResult.Success -> {
                val nextLessonNum = lesson.lessonNumber + 1
                courseRes.value.getLessonByNumber(nextLessonNum)
            }
        }
    }

    fun skipBackward() {
        val currentPosition = audioPlayer.currentPosition
        val newPosition = (currentPosition - SkipBackward.millis).coerceAtLeast(0)
        seekTo(newPosition)
    }

    fun togglePlaybackSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % possibleSpeeds.size
        val playbackSpeed = possibleSpeeds[currentSpeedIndex]
        audioPlayer.setPlaybackSpeed(playbackSpeed)
        refreshUiState()
    }

    fun skipToEnd() {
        val nearEndPosition = audioPlayer.duration - 3000 // a few seconds from end
        seekTo(nearEndPosition)
    }

    fun seekTo(pos: Millis) {
        audioPlayer.seekTo(pos)
        refreshUiState()
    }

    fun jumpForward() {
        val currentPosition = audioPlayer.currentPosition
        val newPosition = (currentPosition + 10000).coerceAtMost(audioPlayer.duration)
        seekTo(newPosition)
    }

    fun seekTo(fraction: Float) {
        if (!debugOptions.allowSeekProgressBar.value) return

        val duration = audioPlayer.duration
        val newPosition = (duration * fraction).toLong()
        seekTo(newPosition)
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
    fun pausePlayback() {
        audioPlayer.pause()
    }

    /**
     * Resumes the paused playback.
     */
    fun resumePlayback() {
        audioPlayer.play()
    }

    private fun getCurrentPlaybackPosition(): Millis {
        return audioPlayer.currentPosition
    }

    private fun getPausepointFractions(): List<Float> {
        val duration = audioPlayer.duration
        if (duration <= 0) return emptyList()
        return pausepoints.map { pausePoint ->
            pausePoint.toFloat() / duration.toFloat()
        }
    }

    private fun refreshUiState() {
        val newState = calculateUiState()
        uiStateMutable.value = newState
    }

    private fun getClosestPausepoint(): Millis? {
        val position = getCurrentPlaybackPosition()
        return pausepoints.minByOrNull { (it - position).absoluteValue }
    }

    private companion object {
        /** Wait a sec after the end of the audio before moving onto the completed state */
        const val completedDelay: Millis = 500

        /** Save the playback position every 5 seconds */
        const val savePositionIntervalMs: Millis = 5000

        /** Only restore a saved position if it is at least this far into the lesson */
        const val minRestoredPositionMs: Millis = 10_000
    }
}