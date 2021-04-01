package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.response.AuthResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("login.php/")
    suspend fun login(
        @Field("userId") userId: String,
        @Field("userPassword") userPassword: String
    ): AuthResponse
    @FormUrlEncoded
    @POST("register.php/")
    suspend fun register(
        @Field("userId") userId: String,
        @Field("userPassword") userPassword: String,
        @Field("userName") userName : String,
        @Field("userNick") userNick : String,
        @Field("userPhone") userPhone : String,
        @Field("userBirth") userBirth : String,
        @Field("userGender") userGender : String,
        @Field("userHeight") userHeight : String,
        @Field("userWeight") userWeight : String
    ): AuthResponse
}