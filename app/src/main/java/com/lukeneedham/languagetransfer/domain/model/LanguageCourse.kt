package com.lukeneedham.languagetransfer.domain.model

/**
 * Data class representing a complete language course with all its audio lessons.
 *
 * @property language The language of the course (e.g., "spanish")
 * @property lessons List of audio lessons in this course
 */
data class LanguageCourse(
    val language: String,
    val lessons: List<CourseLesson>
) {
    /**
     * Returns the total number of lessons in this course.
     */
    val lessonCount: Int
        get() = lessons.size

    fun getLessonByNumber(lessonNumber: Int): CourseLesson? {
        return lessons.firstOrNull { it.lessonNumber == lessonNumber }
    }
}