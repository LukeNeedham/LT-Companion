package com.lukeneedham.languagetransfer.util

import com.lukeneedham.languagetransfer.data.persistence.prefs.DebugPreferencesDao
import com.lukeneedham.languagetransfer.data.persistence.prefs.PrefsBooleanKey

/** Holds debug global overrides for testing */
class DebugOptions(
    private val debugPreferencesDao: DebugPreferencesDao,
) {
    val allLessonsCompleted = get(PrefsBooleanKey.AllLessonsCompleted)
    val showDebugLessonControls = get(PrefsBooleanKey.ShowDebugLessonControls)
    val shouldAutoPause = get(PrefsBooleanKey.ShouldAutoPause)
    val allowSeekProgressBar = get(PrefsBooleanKey.AllowSeekProgressBar)

    fun set(key: PrefsBooleanKey, value: Boolean) {
        debugPreferencesDao.setBoolean(key, value)
    }

    fun get(key: PrefsBooleanKey) = debugPreferencesDao.getBooleanFlow(key)
}