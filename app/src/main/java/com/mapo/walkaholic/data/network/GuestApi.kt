package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.response.*
import retrofit2.http.*

interface GuestApi {
    @GET("global/resource/splash")
    suspend fun getFilenameSplashImage(): FilenameSplashImageResponse

    @GET("global/resource/logo")
    suspend fun getFilenameLogoImage(): FilenameLogoImageResponse

    @GET("global/resource/tutorial")
    suspend fun getFilenameGuideImage(): GuideInformationResponse

    @GET("term/service")
    suspend fun getTermService(): TermServiceResponse

    @GET("term/privacy")
    suspend fun getTermPrivacy(): TermPrivacyResponse

    @Headers(
        "Accept:application/json, text/plain, */*",
        "Content-Type:application/json;charset=UTF-8"
    )

    @GET("oauth/kakao/login")
    suspend fun login(
        @Query("accessToken") accessToken : String
    ): LoginResponse

//    @Headers(
//        "Accept:application/json, text/plain, */*",
//        "Content-Type:application/json;charset=UTF-8"
//    )
//    @POST("login")
//    suspend fun login(
//        @Body userLogin: LoginRequestBody
//    ): RegisterResponse

    /*
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("accessToken") accessToken: String,
        @Field("accessTokenExpiresAt") accessTokenExpiresAt: String,
        @Field("refreshToken") refreshToken: String,
        @Field("refreshTokenExpiresAt") refreshTokenExpiresAt : String
    ): RegisterResponse
     */
}