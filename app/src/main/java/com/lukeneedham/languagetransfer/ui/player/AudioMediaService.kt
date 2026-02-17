package com.lukeneedham.languagetransfer.ui.player

import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.common.util.UnstableApi

@UnstableApi
class AudioMediaService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

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
        mediaSession = MediaSession.Builder(this, p).build()

        // Provide a default media style notification; the service will manage foreground state
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId("audio_playback")
                .setChannelName(com.lukeneedham.languagetransfer.R.string.app_name)
                .build()
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        player?.release()
        player = null
        super.onDestroy()
    }
}
