package com.lukeneedham.languagetransfer.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lukeneedham.languagetransfer.data.persistence.AppDatabase
import com.lukeneedham.languagetransfer.data.persistence.dao.CompletedLessonDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CompletedCourseLessonRepositoryTest {
    private lateinit var completedLessonDao: CompletedLessonDao
    private lateinit var completedLessonRepository: CompletedLessonRepository
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        completedLessonDao = db.completedLessonDao()
        completedLessonRepository = CompletedLessonRepository(completedLessonDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun markLessonAsCompletedTest() = runBlocking {
        // Initially, the lesson is not completed
        assertFalse(completedLessonRepository.isLessonCompleted(1))
        
        // Mark the lesson as completed
        completedLessonRepository.markLessonAsCompleted(1)
        
        // Now the lesson should be marked as completed
        assertTrue(completedLessonRepository.isLessonCompleted(1))
    }

    @Test
    @Throws(Exception::class)
    fun getAllCompletedLessonsTest() = runBlocking {
        // Mark multiple lessons as completed
        completedLessonRepository.markLessonAsCompleted(1)
        completedLessonRepository.markLessonAsCompleted(2)
        completedLessonRepository.markLessonAsCompleted(3)
        
        // Get all completed lessons
        val allLessons = completedLessonRepository.getAllCompletedLessons()
        assertEquals(3, allLessons.size)
    }

    @Test
    @Throws(Exception::class)
    fun getAllCompletedLessonsFlowTest() = runBlocking {
        // Mark multiple lessons as completed
        completedLessonRepository.markLessonAsCompleted(1)
        completedLessonRepository.markLessonAsCompleted(2)
        
        // Get all completed lessons as Flow
        val allLessonsFlow = completedLessonRepository.getAllCompletedLessonsFlow()
        val allLessons = allLessonsFlow.first()
        assertEquals(2, allLessons.size)
    }

    @Test
    @Throws(Exception::class)
    fun getCompletedLessonsCountTest() = runBlocking {
        // Initially, no lessons are completed
        assertEquals(0, completedLessonRepository.getCompletedLessonsCount())
        
        // Mark multiple lessons as completed
        completedLessonRepository.markLessonAsCompleted(1)
        completedLessonRepository.markLessonAsCompleted(2)
        
        // Now there should be 2 completed lessons
        assertEquals(2, completedLessonRepository.getCompletedLessonsCount())
    }

    @Test
    @Throws(Exception::class)
    fun removeLessonFromCompletedTest() = runBlocking {
        // Mark a lesson as completed
        completedLessonRepository.markLessonAsCompleted(1)
        
        // Initially, the lesson is completed
        assertTrue(completedLessonRepository.isLessonCompleted(1))
        
        // Remove the lesson from completed
        completedLessonRepository.removeLessonFromCompleted(1)
        
        // Now the lesson should not be marked as completed
        assertFalse(completedLessonRepository.isLessonCompleted(1))
    }

    @Test
    @Throws(Exception::class)
    fun clearAllCompletedLessonsTest() = runBlocking {
        // Mark multiple lessons as completed
        completedLessonRepository.markLessonAsCompleted(1)
        completedLessonRepository.markLessonAsCompleted(2)
        
        // Initially, there should be 2 completed lessons
        assertEquals(2, completedLessonRepository.getCompletedLessonsCount())
        
        // Clear all completed lessons
        completedLessonRepository.clearAllCompletedLessons()
        
        // Now there should be 0 completed lessons
        assertEquals(0, completedLessonRepository.getCompletedLessonsCount())
    }
}