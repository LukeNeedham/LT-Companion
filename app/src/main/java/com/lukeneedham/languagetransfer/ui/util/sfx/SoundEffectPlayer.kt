package com.lukeneedham.languagetransfer.ui.util.sfx

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.lukeneedham.languagetransfer.R

object SoundEffectPlayer {
    private val soundPool = createSoundPool()
    private var effectToIdMap: Map<SoundEffect, Int> = emptyMap()

    /**
     * Must be called before anything else.
     * Prefer to do this as soon as possible,
     * so everything is loaded and ready to play when desired
     */
    fun prepare(context: Context) {
        fun load(res: Int) = soundPool.load(context, res, 1)

        effectToIdMap = SoundEffect.entries.associate { effect ->
            val res = getRes(effect)
            val id = load(res)
            effect to id
        }
    }

    fun play(effect: SoundEffect, volume: Float = 1f) {
        val id = effectToIdMap[effect] ?: error("No sound id found for effect: $effect")
        play(id, volume)
    }

    private fun getRes(effect: SoundEffect) = when (effect) {
        SoundEffect.Completed -> R.raw.sfx_completed
        SoundEffect.Thump -> R.raw.sfx_thump
    }

    private fun play(id: Int, volume: Float) {
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    private fun createSoundPool(): SoundPool {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        return SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
    }
}