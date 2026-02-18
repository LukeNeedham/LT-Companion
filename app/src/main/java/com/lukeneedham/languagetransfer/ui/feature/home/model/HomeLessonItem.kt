package com.lukeneedham.languagetransfer.ui.feature.home.model

import com.lukeneedham.languagetransfer.domain.model.CourseLesson

data class HomeLessonItem(
    val lesson: CourseLesson,
    val progress: Progress,
    val hasBookmark: Boolean,
) {
    enum class Progress {
        Locked,
        Current,
        Completed,
    }
}