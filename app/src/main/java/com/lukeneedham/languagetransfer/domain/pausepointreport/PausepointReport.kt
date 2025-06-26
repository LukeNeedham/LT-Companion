package com.lukeneedham.languagetransfer.domain.pausepointreport

import com.lukeneedham.languagetransfer.util.model.Millis

sealed interface PausepointReport {
    data class Add(val at: Millis) : PausepointReport
    data class Remove(val pausepoint: Millis) : PausepointReport
    data class TooEarly(val pausepoint: Millis) : PausepointReport
    data class TooLate(val pausepoint: Millis) : PausepointReport
}
