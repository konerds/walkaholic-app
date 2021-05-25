package com.mapo.walkaholic.data.repository

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.LoginRequestBody
import com.mapo.walkaholic.data.model.request.SignupRequestBody
import com.mapo.walkaholic.data.network.GuestApi

class AuthRepository(
    private val api: GuestApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {

    private var userId : Long = 0

    suspend fun getFilenameLogoImage() = safeApiCall {
        api.getFilenameLogoImage()
    }

    suspend fun getTermService() = safeApiCall {
        api.getTermService()
    }

    suspend fun getTermPrivacy() = safeApiCall {
        api.getTermPrivacy()
    }

    suspend fun login(userId: Long) = safeApiCall {
        api.login(LoginRequestBody(userId))
    }

    /*
    suspend fun login(token:OAuthToken) = safeApiCall {
        api.login(token.accessToken, token.accessTokenExpiresAt.toString(), token.refreshToken, token.refreshTokenExpiresAt.toString())
    }
     */

    suspend fun register(
        userBirth: String,
        userGender: String,
        userHeight : String,
        userId: Long,
        userNickname: String,
        userWeight : String
    ) = safeApiCall {
        api.register(SignupRequestBody(userBirth, userGender, userHeight, userId, userNickname, userWeight))
    }
}