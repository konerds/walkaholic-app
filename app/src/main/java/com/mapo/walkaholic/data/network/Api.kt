package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("service")
    suspend fun anyService(): Any

    @FormUrlEncoded
    @POST("auth/kakao/login")
    suspend fun login(): LoginResponse
}