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

    val authToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_AUTH]
        }
    var firstToken: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_FIRST]
    }

    suspend fun saveAuthToken(authToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH] = authToken
        }
    }

    suspend fun saveNotFirst(firstToken: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST] = firstToken
        }
    }

    companion object {
        private val KEY_AUTH = preferencesKey<String>("key_auth")
        private var IS_FIRST = preferencesKey<Boolean>("is_first")
    }
}