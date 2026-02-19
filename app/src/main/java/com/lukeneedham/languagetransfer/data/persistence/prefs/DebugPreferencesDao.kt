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

    /** This has to be cached because we aren't really allowed to create prefs keys on the fly */
    private val booleanKeyToDataFlow =
        PrefsBooleanKey.entries.associateWith { getBooleanFlowInternal(it) }

    fun setBoolean(booleanKey: PrefsBooleanKey, value: Boolean) {
        val prefsKey = getPrefsKey(booleanKey)
        setBoolean(prefsKey, value)
    }

    fun getBooleanFlow(booleanKey: PrefsBooleanKey) =
        booleanKeyToDataFlow[booleanKey] ?: error("Key missing for $booleanKey")

    private fun getBooleanFlowInternal(booleanKey: PrefsBooleanKey): StateFlow<Boolean> {
        val prefsKey = getPrefsKey(booleanKey)
        val default = booleanKey.default
        return getBooleanStateFlow(prefsKey, default)
    }

    private fun getPrefsKey(booleanKey: PrefsBooleanKey): Preferences.Key<Boolean> {
        return booleanPreferencesKey(booleanKey.persistedName)
    }

    private fun getBooleanStateFlow(key: Preferences.Key<Boolean>, default: Boolean) =
        context.dataStore.data
            .map { it[key] ?: default }
            .stateIn(scope, started = SharingStarted.WhileSubscribed(5000), initialValue = default)

    private fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        scope.launch {
            context.dataStore.edit { it[key] = value }
        }
    }
} 