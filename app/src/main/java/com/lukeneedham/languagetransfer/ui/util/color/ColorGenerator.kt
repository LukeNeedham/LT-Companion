package com.lukeneedham.languagetransfer.ui.util.color

import android.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

object ColorGenerator {
    fun generateColorScheme(seed: Int): ColorScheme {
        val baseHueStep = 31

        val baseHue = (baseHueStep * (seed - 1)) % 360

        val random = Random(seed)

        val saturation = 0.6f
        val lightness = 0.7f
        val hueStep = random.nextInt(from = 10, until = 16)
        val colors = generateAnalogousColors(
            baseHue = baseHue,
            count = 3,
            saturation = saturation,
            lightness = lightness,
            hueStep = hueStep.toFloat(),
        ).map { it.toLong() }
        return ColorScheme(colors)
    }

    private fun generateAnalogousColors(
        baseHue: Int,
        count: Int,
        /** 0.0 to 1.0 */
        saturation: Float,
        /** 0.0 to 1.0 */
        lightness: Float,
        /** Degrees between analogous colors (e.g., 30-60) */
        hueStep: Float,
    ): List<Int> {
        require(count > 0) { "Count must be positive" }

        /** Used to center the set of analogous colors around the [baseHue] */
        val middleOffset = (count - 1) / 2f

        return List(count) { i ->
            /** the relative position of the current color [i] with respect to the center of your analogous set */
            val relativePosition = i - middleOffset

            /** Offset from [baseHue] */
            val hueOffset = relativePosition * hueStep

            /** Generated hue */
            val hue = baseHue + hueOffset

            // Ensure [hue] is from 0 to 360 degrees
            // Adding 3600 ensures the result of modulo is always positive, even with negative intermediate results
            val currentHue = (hue + 3600) % 360
            val hslColor = HSL(currentHue, saturation, lightness)
            hslToRgb(hslColor)
        }
    }

    /**
     * Converts an RGB color to HSL.
     * R, G, B values are expected in the range [0, 255].
     * H, S, L values will be in ranges: H [0, 360], S [0, 1], L [0, 1].
     */
    private fun rgbToHsl(red: Int, green: Int, blue: Int): HSL {
        val r = red / 255f
        val g = green / 255f
        val b = blue / 255f

        val max = max(r, max(g, b))
        val min = min(r, min(g, b))
        val delta = max - min

        var h = 0f
        var s = 0f
        val l = (max + min) / 2f

        if (delta != 0f) {
            s = if (l > 0.5f) delta / (2f - max - min) else delta / (max + min)

            h = when (max) {
                r -> ((g - b) / delta + (if (g < b) 6 else 0))
                g -> ((b - r) / delta + 2f)
                else -> ((r - g) / delta + 4f)
            } / 6f
        }
        return HSL(h * 360f, s, l)
    }

    /**
     * Converts an HSL color to RGB.
     * H is expected in range [0, 360].
     * S, L are expected in range [0, 1].
     * R, G, B values will be in range [0, 255].
     */
    private fun hslToRgb(hsl: HSL): Int {
        val h = hsl.h
        val s = hsl.s
        val l = hsl.l

        val r: Float
        val g: Float
        val b: Float

        if (s == 0f) {
            r = l
            g = l
            b = l
        } else {
            val hue2rgb = fun(p: Float, q: Float, t: Float): Float {
                var tempT = t
                if (tempT < 0f) tempT += 1f
                if (tempT > 1f) tempT -= 1f
                if (tempT < 1f / 6f) return p + (q - p) * 6f * tempT
                if (tempT < 1f / 2f) return q
                if (tempT < 2f / 3f) return p + (q - p) * (2f / 3f - tempT) * 6f
                return p
            }

            val q = if (l < 0.5f) l * (1f + s) else l + s - l * s
            val p = 2f * l - q

            r = hue2rgb(p, q, h / 360f + 1f / 3f)
            g = hue2rgb(p, q, h / 360f)
            b = hue2rgb(p, q, h / 360f - 1f / 3f)
        }

        val red = (r * 255).roundToInt()
        val green = (g * 255).roundToInt()
        val blue = (b * 255).roundToInt()

        // On Android, you can use Color.argb(). If not on Android, you'd manually construct the Int.
        return Color.argb(255, red, green, blue)
        // If not using Android's Color class:
        // return (255 shl 24) or (red shl 16) or (green shl 8) or blue
    }

    private data class HSL(var h: Float, var s: Float, var l: Float)
}