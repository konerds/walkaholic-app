package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.response.AuthResponse
import com.mapo.walkaholic.data.model.response.GuideInformationResponse
import com.mapo.walkaholic.data.model.response.TermResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface GuestApi {
    @FormUrlEncoded
    @POST("auth/create")
    suspend fun register(
            @Field("id") id: Long,
            @Field("nickname") nickname: String,
            @Field("birth") birth: Int,
            @Field("gender") gender: Int,
            @Field("height") height: Int,
            @Field("weight") weight: Int
    ): AuthResponse

    @GET("global/guide")
    suspend fun getTutorialFilenames(): GuideInformationResponse

    @GET("auth/term")
    suspend fun getTerm(): TermResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
            @Field("id") id: Long
    ): AuthResponse

    /*
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("accessToken") accessToken: String,
        @Field("accessTokenExpiresAt") accessTokenExpiresAt: String,
        @Field("refreshToken") refreshToken: String,
        @Field("refreshTokenExpiresAt") refreshTokenExpiresAt : String
    ): AuthResponse
     */
}