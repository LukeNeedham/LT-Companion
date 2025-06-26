package com.lukeneedham.languagetransfer.domain.model

import com.lukeneedham.languagetransfer.util.model.Millis

/**
 * Data class representing the pause points for audio lessons.
 * Each key is the name of an audio file, and each value is a list of timestamps in milliseconds.
 */
data class LanguagePausePoints(val audioToPausepoints: Map<String, List<Millis>>)
