package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Api

class AuthRepository(
        private val api: Api,
        private val preferences: UserPreferences
) : BaseRepository() {
    suspend fun login() = safeApiCall {
        api.login()
    }

    suspend fun saveAuthToken(token: String) {
        preferences.saveAuthToken(token)
    }
}