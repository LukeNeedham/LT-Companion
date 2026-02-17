package com.lukeneedham.languagetransfer.ui.player

import android.net.Uri
import com.lukeneedham.languagetransfer.ui.player.AudioPlayer.Callbacks

class AudioPlayerProvider(private val mediaControllerProvider: MediaControllerProvider) {
    fun create(uri: Uri, callbacks: Callbacks): AudioPlayer {
        return AudioPlayer(uri, mediaControllerProvider, callbacks)
    }
}