package com.lukeneedham.languagetransfer.ui.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.AppResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@UnstableApi
class AudioMediaService : MediaSessionService() {

    private val soundEffectPlayer = SoundEffectPlayer

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    private val pausepointHandler = PausepointHandler(soundEffectPlayer) {
        player?.pause()
    }

    // DI
    private val audioLessonRepository: AudioLessonRepository by inject()
    private val lessonPausepointProviderFactory: LessonPausepointProvider.Factory by inject()

    // Scope for collecting pausepoints and checking progress
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var pausepointsJob: Job? = null
    private var checkPausepointsJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        val p = ExoPlayer.Builder(this).build().apply {
            // Configure sensible audio attributes so playback respects device settings
            setAudioAttributes(
                AudioAttributes.DEFAULT,
                /* handleAudioFocus= */ true
            )
        }
        player = p

        p.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                    updatePausepointsForCurrentItem()
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    pausepointHandler.clearHandledPausepoints()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    startPausepointCheck()
                } else {
                    stopPausepointCheck()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    // Ensure pausepoints loaded when ready (covers first item)
                    updatePausepointsForCurrentItem()
                }
                if (playbackState == Player.STATE_ENDED) {
                    stopPausepointCheck()
                }
            }
        })

        if (p.isPlaying) {
            startPausepointCheck()
        }

        mediaSession = MediaSession.Builder(this, p)
            .setCallback(object : MediaSession.Callback {})
            .build()

        // Provide a default media style notification; the service will manage foreground state
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId("audio_playback")
                .setChannelName(com.lukeneedham.languagetransfer.R.string.app_name)
                .build()
        )

        // Attempt initial pausepoints setup (in case item already set before listener fires)
        updatePausepointsForCurrentItem()
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
        val ph = pausepointHandler

        checkPausepointsJob?.cancel()
        checkPausepointsJob = scope.launch {
            while (true) {
                if (p.isPlaying) {
                    ph.checkPausepoints(p.currentPosition)
                }
                delay(10)
            }
        }
    }

    private fun stopPausepointCheck() {
        checkPausepointsJob?.cancel()
        checkPausepointsJob = null
    }

    private fun updatePausepointsForCurrentItem() {
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

            val provider = lessonPausepointProviderFactory.build(lesson)
            provider.pausepoints.collectLatest { pps ->
                pausepointHandler.pausepoints = pps
            }
        }
    }

}
