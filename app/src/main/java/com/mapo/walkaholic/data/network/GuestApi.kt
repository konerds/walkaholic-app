package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.LoginRequestBody
import com.mapo.walkaholic.data.model.request.SignupRequestBody
import com.mapo.walkaholic.data.model.response.*
import retrofit2.http.*

interface GuestApi {
    @GET("global/resource/splash")
    suspend fun getFilenameSplashImage(): FilenameSplashImageResponse

    @GET("global/resource/logo")
    suspend fun getFilenameLogoImage(): FilenameLogoImageResponse

    @GET("global/guide")
    suspend fun getFilenameGuideImage(): GuideInformationResponse

    @GET("term/service")
    suspend fun getTermService(): TermServiceResponse

    @GET("term/privacy")
    suspend fun getTermPrivacy(): TermPrivacyResponse

    @Headers(
        "Accept:application/json, text/plain, */*",
        "Content-Type:application/json;charset=UTF-8"
    )
    @POST("signup")
    suspend fun register(
        @Body userSignUpDto: SignupRequestBody
    ): AuthResponse

    @Headers(
        "Accept:application/json, text/plain, */*",
        "Content-Type:application/json;charset=UTF-8"
    )
    @POST("login")
    suspend fun login(
        @Body userLogin: LoginRequestBody
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