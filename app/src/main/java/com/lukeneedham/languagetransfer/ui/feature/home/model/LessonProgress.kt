package com.lukeneedham.languagetransfer.ui.feature.home.model

import com.lukeneedham.languagetransfer.domain.model.CourseLesson

data class LessonProgress(val lesson: CourseLesson, val progress: Progress) {
    enum class Progress {
        Locked,
        Current,
        Completed,
    }
}