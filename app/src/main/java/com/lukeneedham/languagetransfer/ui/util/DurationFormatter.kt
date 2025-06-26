package com.lukeneedham.languagetransfer.ui.util

import com.lukeneedham.languagetransfer.util.model.Millis
import java.util.concurrent.TimeUnit

object DurationFormatter {
    fun format(milliseconds: Millis): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }
}