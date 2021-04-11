package com.mapo.walkaholic.data.repository

import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.launch

class AuthRepository(
        private val api: Api,
        private val preferences: UserPreferences
) : BaseRepository() {
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

    suspend fun saveAuthToken(id: Long) {
        preferences.saveAuthToken(id)
    }
}