package com.lukeneedham.languagetransfer.data.persistence.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.remove
import androidx.datastore.preferences.preferencesDataStore
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "lesson_progress")

/**
 * Stores the last playback position for each lesson, so users can resume
 * from where they left off when they return to a lesson.
 */
class LessonProgressDao(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)

    /** Returns the saved playback position in milliseconds, or null if none is saved. */
    suspend fun getSavedPosition(lessonNumber: Int): Millis? {
        val key = getPositionKey(lessonNumber)
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }.first()
    }

    /** Saves the current playback position in milliseconds. */
    fun savePosition(lessonNumber: Int, positionMs: Millis) {
        val key = getPositionKey(lessonNumber)
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[key] = positionMs
            }
        }
    }

    /** Clears any saved position for this lesson (e.g. when the lesson is completed). */
    fun clearPosition(lessonNumber: Int) {
        val key = getPositionKey(lessonNumber)
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
    }

    private fun getPositionKey(lessonNumber: Int) = longPreferencesKey("position_$lessonNumber")
}
