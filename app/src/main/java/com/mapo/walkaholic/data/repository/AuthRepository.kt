package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.AuthApi

class AuthRepository(
    private val api: AuthApi,
    private val preferences: UserPreferences
) : BaseRepository() {
    suspend fun login(
        userId: String,
        userPassword: String
    ) = safeApiCall {
        api.login(userId, userPassword)
    }

    suspend fun saveAuthToken(token: String) {
        preferences.saveAuthToken(token)
    }
}