package com.mapo.walkaholic.data.repository

import com.kakao.sdk.auth.model.OAuthToken
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.SignupRequestBody
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.InnerApi

class AuthRepository(
    private val api: GuestApi,
    private val innerApi: InnerApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    suspend fun getFilenameLogoImage() = safeApiCall {
        api.getFilenameLogoImage()
    }

    suspend fun getTermService() = safeApiCall {
        api.getTermService()
    }

    suspend fun getTermPrivacy() = safeApiCall {
        api.getTermPrivacy()
    }

    suspend fun login(token:OAuthToken) = safeApiCall {
        api.login(token.accessToken)
    }

    suspend fun register(
        userBirth: String,
        userGender: String,
        userHeight : String,
        userNickname: String,
        userWeight : String
    ) = safeApiCall {
        innerApi.register(SignupRequestBody(userBirth, userGender, userHeight, userNickname, userWeight))
    }
}