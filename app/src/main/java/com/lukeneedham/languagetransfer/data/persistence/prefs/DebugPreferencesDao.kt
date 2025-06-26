package com.lukeneedham.languagetransfer.data.persistence.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "debug_preferences")

class DebugPreferencesDao(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val allLessonsCompletedKey = booleanPreferencesKey("all_lessons_completed")
    private val showDebugLessonControlsKey = booleanPreferencesKey("show_debug_lesson_controls")

    val allLessonsCompleted: StateFlow<Boolean> = getBooleanStateFlow(allLessonsCompletedKey)
    val showDebugLessonControls: StateFlow<Boolean> =
        getBooleanStateFlow(showDebugLessonControlsKey)

    fun setAllLessonsCompleted(value: Boolean) {
        setBoolean(allLessonsCompletedKey, value)
    }

    fun setShowDebugLessonControls(value: Boolean) {
        setBoolean(showDebugLessonControlsKey, value)
    }

    private fun getBooleanStateFlow(key: Preferences.Key<Boolean>) = context.dataStore.data
        .map { it[key] ?: false }
        .stateIn(scope, started = SharingStarted.WhileSubscribed(5000), initialValue = false)

    private fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        scope.launch {
            context.dataStore.edit { it[key] = value }
        }
    }
} 