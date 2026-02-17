package com.lukeneedham.languagetransfer.ui.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

class MediaControllerProvider(private val context: Context) {
    fun buildAsync(): ListenableFuture<MediaController> {
        val sessionToken = SessionToken(context, ComponentName(context, AudioMediaService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync()
    }
}
