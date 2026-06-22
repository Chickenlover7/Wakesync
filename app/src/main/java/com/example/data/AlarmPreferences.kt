package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "alarm_prefs")

class AlarmPreferences(private val context: Context) {
    companion object {
        val ALARM_TIME = longPreferencesKey("alarm_time")
        val IS_ALARM_SET = booleanPreferencesKey("is_alarm_set")
        val SLEEP_START_TIME = longPreferencesKey("sleep_start_time")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LONGEST_STREAK = intPreferencesKey("longest_streak")
        val LAST_STREAK_RENEWAL = longPreferencesKey("last_streak_renewal")
    }

    val alarmTime: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[ALARM_TIME]
    }

    val isAlarmSet: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ALARM_SET] ?: false
    }
    
    val sleepStartTime: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[SLEEP_START_TIME]
    }
    
    val currentStreak: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[CURRENT_STREAK] ?: 0
    }

    suspend fun setAlarm(timeMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[ALARM_TIME] = timeMillis
            prefs[IS_ALARM_SET] = true
        }
    }

    suspend fun clearAlarm() {
        context.dataStore.edit { prefs ->
            prefs[IS_ALARM_SET] = false
        }
    }
    
    suspend fun setSleepStart(timeMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[SLEEP_START_TIME] = timeMillis
        }
    }
    
    suspend fun clearSleepStart() {
        context.dataStore.edit { prefs ->
            prefs.remove(SLEEP_START_TIME)
        }
    }
    
    suspend fun incrementStreak() {
        context.dataStore.edit { prefs ->
            val current = (prefs[CURRENT_STREAK] ?: 0) + 1
            prefs[CURRENT_STREAK] = current
            
            val longest = prefs[LONGEST_STREAK] ?: 0
            if (current > longest) {
                prefs[LONGEST_STREAK] = current
            }
            
            prefs[LAST_STREAK_RENEWAL] = System.currentTimeMillis()
        }
    }
    
    suspend fun resetStreak() {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_STREAK] = 0
        }
    }
}
