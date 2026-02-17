package com.lukeneedham.languagetransfer.ui.player

import android.net.Uri
import com.lukeneedham.languagetransfer.ui.player.AudioPlayer.Callbacks

class AudioPlayerProvider(
    private val mediaControllerProvider: MediaControllerProvider,
    private val playbackRepository: PlaybackRepository,
) {
    fun create(uri: Uri, lessonNumber: Int, callbacks: Callbacks): AudioPlayer {
        return AudioPlayer(
            uri = uri,
            lessonNumber = lessonNumber,
            callbacks = callbacks,
            mediaControllerProvider = mediaControllerProvider,
            playbackRepository = playbackRepository,
        )
    }
}