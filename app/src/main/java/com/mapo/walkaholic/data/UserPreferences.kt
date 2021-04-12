package com.mapo.walkaholic.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
        context: Context
) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
            name = "local_data_store"
    )

    val authToken: Flow<Long?>
        get() = dataStore.data.map { preferences ->
            preferences[AUTH_ID]
        }

    suspend fun saveAuthToken(id: Long) {
        dataStore.edit { preferences ->
            preferences[AUTH_ID] = id
        }
    }

    companion object {
        private val AUTH_ID = preferencesKey<Long>("auth_id")
    }
}