package com.lukeneedham.languagetransfer.ui.player

import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class PausepointChecker(
    private val pausepointCheckInterval: Millis,
    private val debugOptions: DebugOptions,
) {
    // Ensure that all work is synchronised -
    // every public API needs to handle switching the thread,
    // as the caller could be from anywhere
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(singleThreadDispatcher)

    /** Sorted list of all pausepoints for the lesson */
    private var allPausepoints: List<Millis> = emptyList()

    /** A temporary mute on checks - used to avoid race conditions */
    private var muted = false

    /**
     * Subset of [allPausepoints] with only the points that have NOT been handled.
     * A little bit counter-intuitive compared to tracking the ones that HAVE been handled,
     * but this is a performance optimisation.
     * This list is sorted, so the first item of this list is always the next pause-point.
     */
    private val unhandledPausepoints: MutableList<Millis> = mutableListOf()

    var onPausepointHitListener: () -> Unit = {}

    fun setPausepoints(pausepoints: List<Millis>) {
        scope.launch {
            muted = true
            val sortedPausepoints = pausepoints.sorted()
            allPausepoints = sortedPausepoints
            unhandledPausepoints.clear()
            unhandledPausepoints.addAll(sortedPausepoints)
            muted = false
        }
    }

    fun checkPausepoints(currentPosition: Millis) {
        scope.launch {
            if (muted) return@launch
            if (!debugOptions.shouldAutoPause.value) return@launch

            val nextPausepoint = unhandledPausepoints.firstOrNull() ?: return@launch

            val isHit = isHit(currentPosition = currentPosition, pausepoint = nextPausepoint)
            if (isHit) {
                unhandledPausepoints.remove(nextPausepoint)
                autoPause()
            }
        }
    }

    /**
     * Called when a seek is about to happen -
     * pausepoint checking should be muted until the seek completes.
     * This avoids race conditions, since the pausepoint list will change when the seek completes.
     */
    fun preSeek() {
        scope.launch {
            muted = true
        }
    }

    fun onSeekCompleted(newPosition: Millis) {
        scope.launch {
            muted = true
            val newUnhandledPausepoints = allPausepoints.filter { it > newPosition }
            unhandledPausepoints.clear()
            unhandledPausepoints.addAll(newUnhandledPausepoints)
            muted = false
        }
    }

    private fun isHit(currentPosition: Millis, pausepoint: Millis): Boolean {
        val delta = pausepoint - currentPosition
        // If we're already past the pausepoint, we hit it
        if (delta <= 0) return true
        /** How far we look into the future to consider a pausepoint hit */
        val maxDelta = pausepointCheckInterval * 2
        return delta < maxDelta
    }

    private fun autoPause() {
        if (muted) return
        if (!debugOptions.shouldAutoPause.value) return

        onPausepointHitListener()
    }
}
