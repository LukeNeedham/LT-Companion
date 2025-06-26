package com.lukeneedham.languagetransfer.util

import com.lukeneedham.languagetransfer.data.persistence.prefs.DebugPreferencesDao
import kotlinx.coroutines.flow.StateFlow

/** Holds debug global overrides for testing */
class DebugOptions(
    private val debugPreferencesDao: DebugPreferencesDao,
) {
    val allLessonsCompleted: StateFlow<Boolean> = debugPreferencesDao.allLessonsCompleted
    val showDebugLessonControls: StateFlow<Boolean> = debugPreferencesDao.showDebugLessonControls

    fun setAllLessonsCompleted(value: Boolean) {
        debugPreferencesDao.setAllLessonsCompleted(value)
    }

    fun setShowDebugLessonControls(value: Boolean) {
        debugPreferencesDao.setShowDebugLessonControls(value)
    }
}