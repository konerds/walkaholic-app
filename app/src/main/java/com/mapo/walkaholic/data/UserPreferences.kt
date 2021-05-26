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

    val jwtToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[JWT_TOKEN]
        }

    val isPermissionLocation: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[IS_LOCATION_GRANTED]
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

    suspend fun saveJwtToken(jwtToken: String) {
        dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = jwtToken
        }
    }

    suspend fun saveIsLocationGranted(isLocationGranted : Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOCATION_GRANTED] = isLocationGranted
        }
    }

    suspend fun removeIsFirst() {
        dataStore.edit { preferences ->
            preferences.remove(IS_FIRST)
        }
    }

    suspend fun removeJwtToken() {
        dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN)
        }
    }

    suspend fun removeIsLocationGranted() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOCATION_GRANTED)
        }
    }

    companion object {
        private val IS_FIRST = preferencesKey<Boolean>("is_first")
        private val JWT_TOKEN = preferencesKey<String>("jwt_token")
        private val IS_LOCATION_GRANTED = preferencesKey<Boolean>("is_location_granted")
    }
}