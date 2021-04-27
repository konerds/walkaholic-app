package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.InnerApi

class AuthRepository(
    private val api: InnerApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        @JvmStatic
        fun getInstance(
            api: InnerApi,
            preferences: UserPreferences
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(api, preferences).also {
                    instance = it
                }
            }

    }

    suspend fun getTerm() = safeApiCall {
        api.getTerm()
    }

    suspend fun login(id: Long) = safeApiCall {
        api.login(id)
    }

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