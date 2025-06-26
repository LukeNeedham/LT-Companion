package com.lukeneedham.languagetransfer.ui.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayerProvider(private val context: Context) {
    fun create(uri: Uri): ExoPlayer {
        return ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
        }
    }
}