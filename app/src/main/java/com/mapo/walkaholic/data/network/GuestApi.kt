package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.LoginRequestBody
import com.mapo.walkaholic.data.model.request.SignupRequestBody
import com.mapo.walkaholic.data.model.response.AuthResponse
import com.mapo.walkaholic.data.model.response.GuideInformationResponse
import com.mapo.walkaholic.data.model.response.TermResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface GuestApi {
    @GET("global/guide")
    suspend fun getTutorialFilenames(): GuideInformationResponse

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(
        @Body userLogin: LoginRequestBody
    ): AuthResponse

    @GET("auth/term")
    suspend fun getTerm(): TermResponse

    @Headers("Content-Type: application/json")
    @POST("signup")
    suspend fun register(
        @Body userSignUpDto: SignupRequestBody
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