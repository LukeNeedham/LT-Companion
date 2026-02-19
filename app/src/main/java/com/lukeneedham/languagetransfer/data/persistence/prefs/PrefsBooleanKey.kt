package com.lukeneedham.languagetransfer.data.persistence.prefs

enum class PrefsBooleanKey(
    /** Do NOT change this name - it is used in persistence */
    val persistedName: String,
    val default: Boolean,
) {
    AllLessonsCompleted(
        persistedName = "all_lessons_completed",
        default = false,
    ),
    ShowDebugLessonControls(
        persistedName = "show_debug_lesson_controls",
        default = false,
    ),
    ShouldAutoPause(
        persistedName = "should_auto_pause",
        default = true,
    ),
    AllowSeekProgressBar(
        persistedName = "allow_seek_progress_bar",
        default = false,
    ),
}