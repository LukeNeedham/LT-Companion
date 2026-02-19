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

    /**
     * Subset of [allPausepoints] with only the points that have NOT been handled.
     * A little bit counter-intuitive compared to tracking the ones that HAVE been handled,
     * but this is a performance optimisation.
     * This list is sorted, so the first item of this list is always the next pause-point.
     */
    private val unhandledPausepoints: MutableList<Millis> = mutableListOf()

    var onPausepointHitListener: () -> Unit = {}

    fun setPausepoints(pausepoints: List<Millis>, currentPosition: Millis) {
        scope.launch {
            val sortedPausepoints = pausepoints.sorted()
            allPausepoints = sortedPausepoints

            // Update unhandledPausepoints to be all the new future points
            val newUnhandledPausepoints = sortedPausepoints.filter { it > currentPosition }
            unhandledPausepoints.clear()
            unhandledPausepoints.addAll(newUnhandledPausepoints)
        }
    }

    fun checkPausepoints(currentPosition: Millis) {
        scope.launch {
            if (!debugOptions.shouldAutoPause.value) return@launch

            val nextPausepoint = unhandledPausepoints.firstOrNull() ?: return@launch

            val isHit = isHit(currentPosition = currentPosition, pausepoint = nextPausepoint)
            if (isHit) {
                unhandledPausepoints.remove(nextPausepoint)
                autoPause()
            }
        }
    }

    fun onSeekCompleted(newPosition: Millis) {
        scope.launch {
            val newUnhandledPausepoints = allPausepoints.filter { it > newPosition }
            unhandledPausepoints.clear()
            unhandledPausepoints.addAll(newUnhandledPausepoints)
        }
    }

    private fun isHit(currentPosition: Millis, pausepoint: Millis): Boolean {
        val delta = pausepoint - currentPosition
        // If we're already past the pausepoint, we hit it
        if (delta <= 0) return true
        /** How far we look into the future to consider a pausepoint hit */
        val maxDelta = pausepointCheckInterval * 1.5
        return delta < maxDelta
    }

    private fun autoPause() {
        if (!debugOptions.shouldAutoPause.value) return

        onPausepointHitListener()
    }
}
