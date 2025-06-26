package com.lukeneedham.languagetransfer.ui.feature.lesson

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.domain.pausepointreport.PausepointReport
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState.InProgress.PlayingState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
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

    private var player: ExoPlayer? = null
    private var progressUpdateJob: Job? = null

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
        initializePlayer()
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
        player?.release()
        player = null
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
        val player = player ?: return
        val currentPosition = player.currentPosition
        val newPosition = (currentPosition - 5000).coerceAtLeast(0) // 5 seconds
        player.seekTo(newPosition)
        updatePlaybackPosition()
    }

    fun togglePlaybackSpeed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val player = player ?: return

        currentSpeedIndex = (currentSpeedIndex + 1) % possibleSpeeds.size
        val playbackSpeed = possibleSpeeds[currentSpeedIndex]
        player.setPlaybackSpeed(playbackSpeed)
        refreshUiState()
    }

    fun skipToEnd() {
        val player = player ?: return
        val nearEndPosition = player.duration - 5000 // 5 seconds from end
        player.seekTo(nearEndPosition)
    }

    fun jumpForward() {
        val player = player ?: return
        val currentPosition = player.currentPosition
        val newPosition = (currentPosition + 10000).coerceAtMost(player.duration)
        player.seekTo(newPosition)
        updatePlaybackPosition()
    }

    fun reportPausepointMissing() {
        val position = getCurrentPlaybackPosition() ?: return
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
        player?.pause()
        stopProgressUpdates()

        playingState = PlayingState.Paused(reason)
        refreshUiState()
    }

    private fun getPausepointFractions(): List<Float> {
        val player = player ?: return emptyList()
        val duration = player.duration
        return triggerPausepoints.map { pausePoint ->
            pausePoint.toFloat() / duration.toFloat()
        }
    }

    /**
     * Resumes the paused playback.
     * Also updates the lastProcessedPausePointIndex to handle pausepoints correctly when resuming.
     */
    private fun resumePlayback() {
        player?.play()
        startProgressUpdates()

        playingState = PlayingState.Playing
        refreshUiState()
    }

    /**
     * Starts a coroutine job to update the playback progress periodically.
     */
    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                updatePlaybackPosition()
                delay(progressTickMillis)
            }
        }
    }

    /**
     * Updates the UI state with the current playback position.
     * Also checks if the current position matches any pausepoint and pauses playback if needed.
     */
    private fun updatePlaybackPosition() {
        val player = player ?: return
        val currentPosition = player.currentPosition

        // Check if we need to pause at a pausepoint
        if (player.isPlaying) {
            checkPausepoints(currentPosition)
        }

        refreshUiState()
    }

    private fun getCurrentPlaybackPosition(): Millis? {
        val player = player ?: return null
        return player.currentPosition
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

    /**
     * Stops the progress update job.
     */
    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    /**
     * Initializes the lesson with the given audio lesson.
     * This will prepare the MediaPlayer and start playback automatically.
     */
    private fun initializePlayer() {
        try {
            player = audioPlayerProvider.create(lesson.audioFile.toUri()).apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_READY -> {
                                if (playWhenReady) {
                                    playingState = PlayingState.Playing
                                    refreshUiState()
                                }
                            }

                            Player.STATE_ENDED -> {
                                viewModelScope.launch {
                                    delay(completedDelay)
                                    completedLessonRepository.markLessonAsCompleted(lesson.lessonNumber)
                                    stopProgressUpdates()
                                    isCompleted = true
                                    refreshUiState()
                                }
                            }

                            else -> {
                                // Nothing to do
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        this@LessonViewModel.error = "Error playing audio: ${error.message}"
                        refreshUiState()
                        stopProgressUpdates()
                    }
                })
                prepare()
                play()
            }

            startProgressUpdates()
        } catch (e: IOException) {
            error = "Error loading audio: ${e.message}"
            refreshUiState()
        }
    }

    private fun getCurrentPlaybackSpeed() = possibleSpeeds[currentSpeedIndex]

    private fun calculateUiState(): LessonState {
        if (isCompleted) return LessonState.Completed

        val e = error
        if (e != null) return LessonState.Error(e)

        val player = player ?: return LessonState.Loading
        val playingState = playingState ?: return LessonState.Loading

        return LessonState.InProgress(
            currentPosition = player.currentPosition,
            totalDuration = player.duration,
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
        val position = getCurrentPlaybackPosition() ?: return null
        return pausepoints.minByOrNull { (it - position).absoluteValue }
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
