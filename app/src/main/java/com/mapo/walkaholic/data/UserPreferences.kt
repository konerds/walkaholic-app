package com.mapo.walkaholic.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
        context: Context
) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
            name = "local_data_store"
    )

    val accessToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    suspend fun saveAuthToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val ACCESS_TOKEN = preferencesKey<String>("access_token")
    }
}