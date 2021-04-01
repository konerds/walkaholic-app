package com.mapo.walkaholic.data.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("login.php/")
    suspend fun anyService(
            @Field("userId") userId: String,
            @Field("userPassword") userPassword: String
    ): Any
}