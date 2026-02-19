package com.lukeneedham.languagetransfer.ui.player

import android.util.Log
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis

class PausepointChecker(
    private val pausepointCheckInterval: Millis,
    private val debugOptions: DebugOptions,
) {
    /** Sorted list of all pausepoints for the lesson */
    private var allPausepoints: List<Millis> = emptyList()

    /**
     * Subset of [allPausepoints] with only the points that have NOT been handled.
     * A little bit counter-intuitive compared to tracking the ones that HAVE been handled,
     * but this is a performance optimisation.
     * This list is sorted, so the first item of this list is always the next pause-point.
     */
    private val unhandledPausepoints: MutableList<Millis> = mutableListOf()

    var onPausepointHitListener: () -> Unit = {}

    fun setPausepoints(pausepoints: List<Millis>) {
        Log.e("luke_pp", "setPausepoints")
        val sortedPausepoints = pausepoints.sorted()
        allPausepoints = sortedPausepoints
        unhandledPausepoints.clear()
        unhandledPausepoints.addAll(sortedPausepoints)
    }

    fun checkPausepoints(currentPosition: Millis) {
        Log.e("luke_pp", "checkPausepoints")

        if (!debugOptions.shouldAutoPause.value) return

        val nextPausepoint = unhandledPausepoints.firstOrNull() ?: return

        val isHit = isHit(currentPosition = currentPosition, pausepoint = nextPausepoint)
        if (isHit) {
            unhandledPausepoints.remove(nextPausepoint)
            autoPause()
        }
    }

    fun onSeek(newPosition: Millis) {
        val newUnhandledPausepoints = allPausepoints.filter { it > newPosition }
        unhandledPausepoints.clear()
        unhandledPausepoints.addAll(newUnhandledPausepoints)
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
        onPausepointHitListener()
    }
}
