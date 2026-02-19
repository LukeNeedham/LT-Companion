package com.lukeneedham.languagetransfer.ui.util.sfx

import android.content.Context

/** Used by the app - separate class for the sake of DI */
class AppSoundEffectPlayer {
    private var player:  SoundEffectPlayer? = null

    fun prepare(context: Context) {
        player = SoundEffectPlayer(context)
    }

    fun play(effect: SoundEffect, volume: Float = 1f) {
        val p = player ?: error("Player not prepared")
        p.play(effect, volume)
    }
}