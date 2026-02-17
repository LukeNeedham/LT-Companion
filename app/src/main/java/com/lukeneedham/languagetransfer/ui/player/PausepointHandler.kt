package com.lukeneedham.languagetransfer.ui.player

import androidx.media3.common.Player
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.model.Millis

class PausepointHandler(
    private val soundEffectPlayer: SoundEffectPlayer,
    private val onPausepointHit: () -> Unit,
) {
    var pausepoints: List<Millis> = emptyList()
        set(value) {
            field = value
            handledPausepoints.clear()
        }

    private val triggerPausepoints: List<Millis>
        get() = pausepoints.map { (it - pausepointTriggerOffset).coerceAtLeast(0) }

    private val handledPausepoints = mutableListOf<Millis>()

    fun checkPausepoints(currentPosition: Millis) {
        val nextPausepoint = triggerPausepoints.firstOrNull { pausepoint ->
            pausepoint > currentPosition
        } ?: return

        val millisUntilPausepoint = nextPausepoint - currentPosition

        val maxMillisDelta = progressTickMillis * 2
        if (millisUntilPausepoint > maxMillisDelta) return

        if (nextPausepoint in handledPausepoints) return

        handledPausepoints.add(nextPausepoint)

        soundEffectPlayer.play(SoundEffect.Thump, volume = 0.1f)
        onPausepointHit()
    }

    fun clearHandledPausepoints() {
        handledPausepoints.clear()
    }

    private companion object {
        const val progressTickMillis: Millis = 10
        const val pausepointTriggerOffset: Millis = 0
    }
}
