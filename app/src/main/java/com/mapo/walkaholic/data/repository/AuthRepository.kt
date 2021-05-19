package com.mapo.walkaholic.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.gson.Gson
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.LoginRequestBody
import com.mapo.walkaholic.data.model.request.SignupRequestBody
import com.mapo.walkaholic.data.network.GuestApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class AuthRepository(
    private val api: GuestApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    suspend fun getTerm() = safeApiCall {
        api.getTerm()
    }

    suspend fun login(id: Long) = safeApiCall {
        api.login(LoginRequestBody(id))
    }

    /*
    suspend fun login(token:OAuthToken) = safeApiCall {
        api.login(token.accessToken, token.accessTokenExpiresAt.toString(), token.refreshToken, token.refreshTokenExpiresAt.toString())
    }
     */

    suspend fun register(
        userBirth : String,
        userGender : String,
        userHeight : String,
        userUid : Long,
        userNickname : String,
        userWeight : String
    ) = safeApiCall {
        api.register(SignupRequestBody(userBirth, userGender, userHeight, userUid, userNickname, userWeight))
    }
}