package com.lukeneedham.languagetransfer.data.persistence.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "pausepoint_modifications")

class PausepointModificationsDao(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getModifiedPausepoints(lessonName: String): List<Long>? {
        val key = getModifiedPausepointsKey(lessonName)
        return context.dataStore.data.map { preferences ->
            read(preferences, key)
        }.first()
    }

    private fun read(preferences: Preferences, key: Preferences.Key<String>): List<Millis>? {
        val persistedString = preferences[key] ?: ""
        if (persistedString.isEmpty()) return null
        return persistedString.split(",").map { it.toLong() }
    }

    fun setModifiedPausepoints(lessonName: String, pausepoints: List<Millis>) {
        val key = getModifiedPausepointsKey(lessonName)
        scope.launch {
            context.dataStore.edit { preferences ->
                val persisted = pausepoints.joinToString(",")
                preferences[key] = persisted
            }
        }
    }

    fun resetAll() {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    private fun getModifiedPausepointsKey(lessonName: String) =
        stringPreferencesKey(modifiedPausepointsKeyPrefix + lessonName)

    companion object {
        val modifiedPausepointsKeyPrefix = "modified_pausepoints_"
    }
}