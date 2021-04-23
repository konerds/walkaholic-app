package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Api

class AuthRepository(
        private val api: Api,
        private val preferences: UserPreferences
) : BaseRepository() {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        @JvmStatic
        fun getInstance(api: Api,
                        preferences: UserPreferences): AuthRepository =
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
            name: String,
            nickname: String,
            birth: Int,
            gender: Int,
            height: Int,
            weight: Int
    ) = safeApiCall {
        api.register(id, name, nickname, birth, gender, height, weight)
    }

    suspend fun saveAuthToken(accessToken: String) {
        preferences.saveAuthToken(accessToken)
    }
}