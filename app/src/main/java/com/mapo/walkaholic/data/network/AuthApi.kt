package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("userId") userId: String,
        @Field("userPassword") userPassword: String
    ): LoginResponse
}