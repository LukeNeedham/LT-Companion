package com.lukeneedham.languagetransfer.domain.model

import kotlinx.serialization.Serializable

/**
 * Data class representing the name of a lesson, as loaded from the lesson names JSON file.
 *
 * @property lessonId The ID of the lesson, matching the audio file name without extension
 * @property lessonName The human-readable name of the lesson
 */
@Serializable
data class LessonName(
    val lessonId: String,
    val lessonName: String,
)
