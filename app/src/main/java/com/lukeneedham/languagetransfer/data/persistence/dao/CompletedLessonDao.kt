package com.lukeneedham.languagetransfer.data.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lukeneedham.languagetransfer.data.persistence.entity.CompletedLessonEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the CompletedLessonEntity.
 * This interface defines the database operations that can be performed on CompletedLessonEntity.
 */
@Dao
interface CompletedLessonDao {
    /**
     * Insert a completed lesson into the database.
     * If a lesson with the same ID already exists, it will be replaced.
     *
     * @param lesson The completed lesson to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLesson(lesson: CompletedLessonEntity)

    /**
     * Insert multiple completed lessons into the database.
     * If lessons with the same IDs already exist, they will be replaced.
     *
     * @param lessons The list of completed lessons to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLessons(lessons: List<CompletedLessonEntity>)

    /**
     * Update a completed lesson in the database.
     *
     * @param lesson The completed lesson to update
     */
    @Update
    suspend fun updateCompletedLesson(lesson: CompletedLessonEntity)

    /**
     * Delete a completed lesson from the database.
     *
     * @param lesson The completed lesson to delete
     */
    @Delete
    suspend fun deleteCompletedLesson(lesson: CompletedLessonEntity)

    /**
     * Get a completed lesson by its ID.
     *
     * @param lessonId The ID of the lesson to retrieve
     * @return The completed lesson with the specified ID, or null if not found
     */
    @Query("SELECT * FROM completed_lessons WHERE lessonId = :lessonId")
    suspend fun getCompletedLessonById(lessonId: Int): CompletedLessonEntity?

    /**
     * Get all completed lessons from the database.
     *
     * @return A list of all completed lessons
     */
    @Query("SELECT * FROM completed_lessons")
    suspend fun getAllCompletedLessons(): List<CompletedLessonEntity>

    /**
     * Get all completed lessons as a Flow, which will emit a new value whenever the data changes.
     *
     * @return A Flow of all completed lessons
     */
    @Query("SELECT * FROM completed_lessons")
    fun getAllCompletedLessonsFlow(): Flow<List<CompletedLessonEntity>>

    /**
     * Check if a lesson is completed.
     *
     * @param lessonId The ID of the lesson to check
     * @return True if the lesson is completed, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM completed_lessons WHERE lessonId = :lessonId)")
    suspend fun isLessonCompleted(lessonId: Int): Boolean

    /**
     * Get the count of completed lessons.
     *
     * @return The number of completed lessons
     */
    @Query("SELECT COUNT(*) FROM completed_lessons")
    suspend fun getCompletedLessonsCount(): Int

    /**
     * Delete all completed lessons from the database.
     */
    @Query("DELETE FROM completed_lessons")
    suspend fun deleteAllCompletedLessons()
}