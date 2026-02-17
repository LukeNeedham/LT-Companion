package com.lukeneedham.languagetransfer.ui.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Thin abstraction around Media3 MediaController used by the UI layer.
 * Hides async controller construction and exposes a very small surface needed by the ViewModel.
 */
class AudioPlayer(
    private val uri: Uri,
    private val mediaControllerProvider: MediaControllerProvider,
    private val callbacks: Callbacks,
) {
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    interface Callbacks {
        fun onReady(playWhenReady: Boolean)
        fun onEnded()
        fun onError(message: String)
        fun onProgressUpdate(position: Long)
    }

    init {
        initialize()
    }

    private fun initialize() {
        // If we already have a controller, reuse it by just setting media item again
        val existing = controller
        if (existing != null) {
            wireCallbacks(existing, callbacks)
            existing.setMediaItem(MediaItem.fromUri(uri))
            existing.prepare()
            existing.play()
            return
        }

        val future = mediaControllerProvider.buildAsync()
        controllerFuture = future
        future.addListener({
            scope.launch {
                try {
                    val c = future.get()
                    controller = c
                    wireCallbacks(c, callbacks)
                    c.setMediaItem(MediaItem.fromUri(uri))
                    c.prepare()
                    c.play()
                } catch (e: Exception) {
                    callbacks.onError(e.message ?: "Unknown error")
                }
            }
        }, { it.run() })
    }

    private fun wireCallbacks(c: MediaController, callbacks: Callbacks) {
        // Remove any previous listeners by creating a new one (MediaController doesn't support removeAll straightforwardly),
        // consumer will recreate callbacks per initialize call.
        c.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        callbacks.onReady(c.playWhenReady)
                        if (c.playWhenReady) {
                            startProgressUpdates(callbacks)
                        } else {
                            stopProgressUpdates()
                        }
                    }

                    Player.STATE_ENDED -> {
                        stopProgressUpdates()
                        callbacks.onEnded()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    startProgressUpdates(callbacks)
                } else {
                    stopProgressUpdates()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                callbacks.onError(error.message ?: "Playback error")
            }
        })
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
        stopProgressUpdates()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        controller?.setPlaybackSpeed(speed)
    }

    private fun startProgressUpdates(callbacks: Callbacks) {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                val pos = controller?.currentPosition ?: 0L
                callbacks.onProgressUpdate(pos)
                delay(10) // Matches LessonViewModel.progressTickMillis
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    val isPlaying: Boolean get() = controller?.isPlaying == true
    val currentPosition: Long get() = controller?.currentPosition ?: 0L
    val duration: Long get() = controller?.duration ?: 0L

    fun release() {
        controllerFuture?.cancel(true)
        controllerFuture = null
        controller?.release()
        controller = null
    }
}