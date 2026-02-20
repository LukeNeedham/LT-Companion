package com.lukeneedham.languagetransfer.ui.player

import android.content.Intent
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.core.content.IntentCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProviderCache
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class AudioMediaService : MediaSessionService() {

    private var player: Player? = null
    private var mediaSession: MediaSession? = null

    // DI
    private val audioLessonRepository: AudioLessonRepository by inject()
    private val lessonPausepointProviderCache: LessonPausepointProviderCache by inject()
    private val playbackRepository: PlaybackRepository by inject()
    private val debugOptions: DebugOptions by inject()

    private val pausepointChecker = PausepointChecker(pausepointCheckInterval, debugOptions)

    // Scope for collecting pausepoints and checking progress
    private val scope = CoroutineScope(Dispatchers.Default)
    private var pausepointsJob: Job? = null
    private var checkPausepointsJob: Job? = null

    // Track the reason for the most recent pause (if initiated internally, e.g., pausepoint)
    private var lastPauseReason: PlayingState.Paused.Reason? = null

    override fun onCreate() {
        super.onCreate()

        /**
         * SoundEffectPlayer using the context of the service.
         * Created in onCreate so context is valid.
         */
        val soundEffectPlayer = SoundEffectPlayer(this)

        pausepointChecker.onPausepointHitListener = {
            // This needs to run on main
            scope.launch {
                withContext(Dispatchers.Main) {
                    soundEffectPlayer.play(SoundEffect.Thump, volume = 0.1f)
                    lastPauseReason = PlayingState.Paused.Reason.Auto
                    player?.pause()
                }
            }
        }

        val player = createAudioPlayer()
        this.player = player

        if (player.isPlaying) {
            startPausepointCheck()
        }

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(createMediaSessionCallback())
            .build()

        setupNotification()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        pausepointsJob?.cancel()
        checkPausepointsJob?.cancel()
        mediaSession?.release()
        mediaSession = null
        player?.release()
        player = null
        super.onDestroy()
    }

    private fun startPausepointCheck() {
        val p = player ?: return

        checkPausepointsJob?.cancel()
        checkPausepointsJob = scope.launch {
            while (true) {
                // Reading player position requires main thread
                val position = withContext(Dispatchers.Main) {
                    p.currentPosition
                }
                pausepointChecker.checkPausepoints(position)
                delay(pausepointCheckInterval)
            }
        }
    }

    private fun stopPausepointCheck() {
        checkPausepointsJob?.cancel()
        checkPausepointsJob = null
    }

    private fun observePausepointsForCurrentItem() {
        val p = player ?: return
        val mediaId = p.currentMediaItem?.mediaId ?: return
        val lessonNumber = mediaId.toIntOrNull() ?: return

        // Cancel any ongoing collection before starting a new one
        pausepointsJob?.cancel()
        pausepointsJob = scope.launch {
            // Fetch course and find lesson
            val courseResult = audioLessonRepository.getLanguageCourse()
            val lesson = when (courseResult) {
                is AppResult.Success ->
                    courseResult.value.getLessonByNumber(lessonNumber)

                is AppResult.Failure -> null
            } ?: return@launch

            val provider = lessonPausepointProviderCache.get(lesson)
            provider.pausepoints.collectLatest {
                val currentPosition = withContext(Dispatchers.Main) {
                    p.currentPosition
                }

                pausepointChecker.setPausepoints(
                    pausepoints = it,
                    currentPosition = currentPosition,
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun setupNotification() {
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId("audio_playback")
                .setChannelName(R.string.app_name)
                .build()
        )
    }

    @OptIn(UnstableApi::class)
    private fun createAudioPlayer(): Player {
        val exoPlayer = ExoPlayer.Builder(this)
            // So media notification can skip backwards
            .setSeekBackIncrementMs(SkipBackward.millis)
            .build().apply {
                // Configure sensible audio attributes so playback respects device settings
                setAudioAttributes(
                    AudioAttributes.DEFAULT,
                    /* handleAudioFocus= */ true
                )
            }

        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                observePausepointsForCurrentItem()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int,
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    pausepointChecker.onSeekCompleted(newPosition.positionMs)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    // Resumed
                    playbackRepository.onStateUpdate(PlayingState.Playing)
                    startPausepointCheck()
                } else {
                    // Paused: reason is Auto if set by pausepoint handler, otherwise Manual
                    val reason = lastPauseReason ?: PlayingState.Paused.Reason.Manual
                    // Reset reason
                    lastPauseReason = null

                    playbackRepository.onStateUpdate(PlayingState.Paused(reason))
                    stopPausepointCheck()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    stopPausepointCheck()
                }
            }
        })

        return exoPlayer
    }

    @OptIn(UnstableApi::class)
    private fun createMediaSessionCallback() = object : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val superRes = super.onConnect(session, controller)

            /** Is [session] the system Media notification? */
            val isMediaNotification = session.isMediaNotificationController(controller)

            /** Is [session] coming from Android Auto? */
            val isAndroidAuto = session.isAutoCompanionController(controller)

            val isThisApp = !isMediaNotification && !isAndroidAuto
            // If the requester is this app, we can do everything - return unchanged
            if (isThisApp) return superRes

            // Otherwise we need to strip out all the undesired commands
            // for requesters like the Media Notification
            val availablePlayerCommands = superRes.availablePlayerCommands.buildUpon().apply {
                // Remove the ability to scrub the timeline manually
                remove(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)

                // Remove the "Jump Forward" command
                remove(Player.COMMAND_SEEK_FORWARD)

                // Remove skipping to the next / previous item
                remove(Player.COMMAND_SEEK_TO_NEXT)
                remove(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                remove(Player.COMMAND_SEEK_TO_PREVIOUS)
                remove(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)

                // Add seek backwards
                add(Player.COMMAND_SEEK_BACK)
            }

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailablePlayerCommands(availablePlayerCommands.build())
                .build()
        }

        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent,
        ): Boolean {
            val p = player
            val isPlaying = p != null && p.mediaItemCount > 0
            // Delegate to system if currently playing
            if (isPlaying) return false

            val keyEvent = IntentCompat.getParcelableExtra(
                intent,
                Intent.EXTRA_KEY_EVENT,
                KeyEvent::class.java,
            )

            val unhandledResumeKeycodes = listOf(
                KeyEvent.KEYCODE_MEDIA_PLAY,
                KeyEvent.KEYCODE_MEDIA_PAUSE,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_HEADSETHOOK,
                KeyEvent.KEYCODE_MEDIA_NEXT,
            )
            val isUnhandledResume =
                keyEvent?.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode in unhandledResumeKeycodes

            // Delegate to rest of app
            if (isUnhandledResume) {
                playbackRepository.onUnhandledResumeEvent()
                return true
            }

            // Not delegated to app, let system handle it
            return false
        }
    }

    companion object {
        const val pausepointCheckInterval: Millis = 100
    }
}
