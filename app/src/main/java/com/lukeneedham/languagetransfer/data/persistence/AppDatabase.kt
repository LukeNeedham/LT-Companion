package com.lukeneedham.languagetransfer.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukeneedham.languagetransfer.data.persistence.dao.CompletedLessonDao
import com.lukeneedham.languagetransfer.data.persistence.entity.CompletedLessonEntity

/**
 * The Room database for the application.
 * This database holds all the entities for the app, including completed lessons.
 */
@Database(
    entities = [CompletedLessonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Get the DAO for completed lessons.
     *
     * @return The CompletedLessonDao
     */
    abstract fun completedLessonDao(): CompletedLessonDao

    companion object {
        /**
         * The name of the database file.
         */
        const val DATABASE_NAME = "language_transfer_db"

        fun build(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME,
        ).build()
    }
}