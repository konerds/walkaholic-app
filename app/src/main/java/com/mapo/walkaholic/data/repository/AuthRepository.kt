package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi

class AuthRepository(
    private val api: GuestApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    suspend fun getTerm() = safeApiCall {
        api.getTerm()
    }

    suspend fun login(id: Long) = safeApiCall {
        api.login(id)
    }

    /*
    suspend fun login(token:OAuthToken) = safeApiCall {
        api.login(token.accessToken, token.accessTokenExpiresAt.toString(), token.refreshToken, token.refreshTokenExpiresAt.toString())
    }
     */

    suspend fun register(
        id: Long,
        nickname: String,
        birth: Int,
        gender: Int,
        height: Int,
        weight: Int
    ) = safeApiCall {
        api.register(id, nickname, birth, gender, height, weight)
    }
}