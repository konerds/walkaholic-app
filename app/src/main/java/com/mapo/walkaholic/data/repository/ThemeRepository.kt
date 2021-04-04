package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences

class ThemeRepository(
        private val api: Api,
        private val preferences: UserPreferences
) : BaseRepository() {
    suspend fun anyService(
            par1: String,
            par2: String
    ) = safeApiCall {
        api.anyService(par1, par2)
    }

    suspend fun saveAuthToken(token: String) {
        preferences.saveAuthToken(token)
    }
}