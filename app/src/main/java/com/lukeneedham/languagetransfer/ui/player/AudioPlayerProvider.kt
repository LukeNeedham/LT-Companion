package com.lukeneedham.languagetransfer.ui.player

import android.net.Uri
import com.lukeneedham.languagetransfer.ui.player.AudioPlayer.Callbacks

class AudioPlayerProvider(private val mediaControllerProvider: MediaControllerProvider) {
    fun create(uri: Uri, lessonNumber: Int, callbacks: Callbacks): AudioPlayer {
        return AudioPlayer(uri, lessonNumber, mediaControllerProvider, callbacks)
    }
}