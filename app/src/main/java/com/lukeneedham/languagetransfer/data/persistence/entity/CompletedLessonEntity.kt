package com.lukeneedham.languagetransfer.data.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a completed lesson in the database.
 *
 * @property lessonId The ID of the lesson (lesson number)
 * @property completedAtMillis The timestamp when the lesson was completed
 */
@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey
    val lessonId: Int,
    val completedAtMillis: Long,
)