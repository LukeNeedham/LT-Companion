package com.lukeneedham.languagetransfer.data.repository

import com.lukeneedham.languagetransfer.data.persistence.dao.CompletedLessonDao
import com.lukeneedham.languagetransfer.data.persistence.entity.CompletedLessonEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository for managing completed lessons.
 * This class provides a clean API for the rest of the app to interact with completed lessons data.
 *
 * @property completedLessonDao The DAO for accessing completed lessons in the database
 */
class CompletedLessonRepository(
    private val completedLessonDao: CompletedLessonDao
) {
    /**
     * Mark a lesson as completed.
     *
     * @param lessonId The ID of the lesson to mark as completed
     */
    suspend fun markLessonAsCompleted(lessonId: Int) {
        val completedLesson = CompletedLessonEntity(
            lessonId = lessonId,
            completedAtMillis = System.currentTimeMillis()
        )
        completedLessonDao.insertCompletedLesson(completedLesson)
    }

    /**
     * Check if a lesson is completed.
     *
     * @param lessonId The ID of the lesson to check
     * @return True if the lesson is completed, false otherwise
     */
    suspend fun isLessonCompleted(lessonId: Int): Boolean {
        return completedLessonDao.isLessonCompleted(lessonId)
    }

    /**
     * Get all completed lessons.
     *
     * @return A list of all completed lessons
     */
    suspend fun getAllCompletedLessons(): List<CompletedLessonEntity> {
        return completedLessonDao.getAllCompletedLessons()
    }

    /**
     * Get all completed lessons as a Flow, which will emit a new value whenever the data changes.
     *
     * @return A Flow of all completed lessons
     */
    fun getAllCompletedLessonsFlow(): Flow<List<CompletedLessonEntity>> {
        return completedLessonDao.getAllCompletedLessonsFlow()
    }

    /**
     * Get the count of completed lessons.
     *
     * @return The number of completed lessons
     */
    suspend fun getCompletedLessonsCount(): Int {
        return completedLessonDao.getCompletedLessonsCount()
    }

    /**
     * Remove a lesson from the completed lessons.
     *
     * @param lessonId The ID of the lesson to remove
     */
    suspend fun removeLessonFromCompleted(lessonId: Int) {
        val lesson = completedLessonDao.getCompletedLessonById(lessonId)
        lesson?.let {
            completedLessonDao.deleteCompletedLesson(it)
        }
    }

    /**
     * Clear all completed lessons.
     */
    suspend fun clearAllCompletedLessons() {
        completedLessonDao.deleteAllCompletedLessons()
    }
}