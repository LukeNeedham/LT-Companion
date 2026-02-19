package com.lukeneedham.languagetransfer.ui.player

import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.model.Millis

class PausepointHandler(
    private val soundEffectPlayer: SoundEffectPlayer,
    private val debugOptions: DebugOptions,
) {
    var pausepoints: List<Millis> = emptyList()
        set(value) {
            field = value
            handledPausepoints.clear()
        }

    var onPausepointHitListener: () -> Unit = {}

    private val handledPausepoints = mutableListOf<Millis>()

    fun checkPausepoints(currentPosition: Millis) {
        val nextPausepoint = pausepoints.firstOrNull { pausepoint ->
            pausepoint > currentPosition
        } ?: return

        val millisUntilPausepoint = nextPausepoint - currentPosition

        val maxMillisDelta = progressTickMillis * 2
        if (millisUntilPausepoint > maxMillisDelta) return

        if (nextPausepoint in handledPausepoints) return

        handledPausepoints.add(nextPausepoint)
        autoPause()
    }

    fun clearHandledPausepoints() {
        handledPausepoints.clear()
    }

    private fun autoPause() {
        if (!debugOptions.shouldAutoPause.value) return
        soundEffectPlayer.play(SoundEffect.Thump, volume = 0.1f)
        onPausepointHitListener()
    }

    private companion object {
        const val progressTickMillis: Millis = 10
    }
}
