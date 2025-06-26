package com.lukeneedham.languagetransfer.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lukeneedham.languagetransfer.data.persistence.dao.CompletedLessonDao
import com.lukeneedham.languagetransfer.data.persistence.entity.CompletedLessonEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CompletedCourseLessonDaoTest {
    private lateinit var completedLessonDao: CompletedLessonDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        completedLessonDao = db.completedLessonDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetCompletedLesson() = runBlocking {
        val completedLesson = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        completedLessonDao.insertCompletedLesson(completedLesson)
        val retrievedLesson = completedLessonDao.getCompletedLessonById(1)
        assertNotNull(retrievedLesson)
        assertEquals(1, retrievedLesson?.lessonId)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteCompletedLesson() = runBlocking {
        val completedLesson = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        completedLessonDao.insertCompletedLesson(completedLesson)
        val retrievedLesson = completedLessonDao.getCompletedLessonById(1)
        assertNotNull(retrievedLesson)
        
        completedLessonDao.deleteCompletedLesson(retrievedLesson!!)
        val deletedLesson = completedLessonDao.getCompletedLessonById(1)
        assertNull(deletedLesson)
    }

    @Test
    @Throws(Exception::class)
    fun isLessonCompletedTest() = runBlocking {
        // Initially, no lessons are completed
        assertFalse(completedLessonDao.isLessonCompleted(1))
        
        // Mark lesson as completed
        val completedLesson = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        completedLessonDao.insertCompletedLesson(completedLesson)
        
        // Now the lesson should be marked as completed
        assertTrue(completedLessonDao.isLessonCompleted(1))
    }

    @Test
    @Throws(Exception::class)
    fun getAllCompletedLessonsTest() = runBlocking {
        // Insert multiple completed lessons
        val lesson1 = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        val lesson2 = CompletedLessonEntity(
            lessonId = 2,
            completedAtMillis = System.currentTimeMillis()
        )
        val lesson3 = CompletedLessonEntity(
            lessonId = 3,
            completedAtMillis = System.currentTimeMillis()
        )
        
        completedLessonDao.insertCompletedLessons(listOf(lesson1, lesson2, lesson3))
        
        // Get all completed lessons
        val allLessons = completedLessonDao.getAllCompletedLessons()
        assertEquals(3, allLessons.size)
    }

    @Test
    @Throws(Exception::class)
    fun getAllCompletedLessonsFlowTest() = runBlocking {
        // Insert multiple completed lessons
        val lesson1 = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        val lesson2 = CompletedLessonEntity(
            lessonId = 2,
            completedAtMillis = System.currentTimeMillis()
        )
        
        completedLessonDao.insertCompletedLessons(listOf(lesson1, lesson2))
        
        // Get all completed lessons as Flow
        val allLessonsFlow = completedLessonDao.getAllCompletedLessonsFlow()
        val allLessons = allLessonsFlow.first()
        assertEquals(2, allLessons.size)
    }

    @Test
    @Throws(Exception::class)
    fun getCompletedLessonsCountTest() = runBlocking {
        // Initially, no lessons are completed
        assertEquals(0, completedLessonDao.getCompletedLessonsCount())
        
        // Insert multiple completed lessons
        val lesson1 = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        val lesson2 = CompletedLessonEntity(
            lessonId = 2,
            completedAtMillis = System.currentTimeMillis()
        )
        
        completedLessonDao.insertCompletedLessons(listOf(lesson1, lesson2))
        
        // Now there should be 2 completed lessons
        assertEquals(2, completedLessonDao.getCompletedLessonsCount())
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllCompletedLessonsTest() = runBlocking {
        // Insert multiple completed lessons
        val lesson1 = CompletedLessonEntity(
            lessonId = 1,
            completedAtMillis = System.currentTimeMillis()
        )
        val lesson2 = CompletedLessonEntity(
            lessonId = 2,
            completedAtMillis = System.currentTimeMillis()
        )
        
        completedLessonDao.insertCompletedLessons(listOf(lesson1, lesson2))
        
        // Initially, there should be 2 completed lessons
        assertEquals(2, completedLessonDao.getCompletedLessonsCount())
        
        // Delete all completed lessons
        completedLessonDao.deleteAllCompletedLessons()
        
        // Now there should be 0 completed lessons
        assertEquals(0, completedLessonDao.getCompletedLessonsCount())
    }
}