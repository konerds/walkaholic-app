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

    val isFirst: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[IS_FIRST]
        }

    suspend fun saveIsFirst(isFirst: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST] = isFirst
        }
    }

    suspend fun saveAuthToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun removeIsFirst() {
        dataStore.edit { preferences ->
            preferences.remove(IS_FIRST)
        }
    }

    suspend fun removeAuthToken() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
        }
    }

    companion object {
        private val IS_FIRST = preferencesKey<Boolean>("is_first")
        private val ACCESS_TOKEN = preferencesKey<String>("access_token")
    }
}