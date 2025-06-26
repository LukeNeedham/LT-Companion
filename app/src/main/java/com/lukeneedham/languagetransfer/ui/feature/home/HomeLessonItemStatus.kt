package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.ui.graphics.Color

sealed interface HomeLessonItemStatus {
    object Future : HomeLessonItemStatus
    data class Current(val colors: List<Color>) : HomeLessonItemStatus
    data class Completed(val colors: List<Color>) : HomeLessonItemStatus
}