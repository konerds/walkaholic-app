package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface InnerApi {
    @GET("auth/logout")
    suspend fun logout(): ResponseBody

    @FormUrlEncoded
    @POST("info/user")
    suspend fun getUser(
            @Field("id") id: Long
    ): UserResponse

    @FormUrlEncoded
    @POST("info/character")
    suspend fun getCharacter(
            @Field("id") id: Long
    ): UserCharacterResponse

    @FormUrlEncoded
    @POST("info/exptable")
    suspend fun getExpTable(
            @Field("exp") exp: Long
    ): ExpTableResponse

    @GET("info/themelist")
    suspend fun getThemeEnum() : ThemeEnumResponse

    @FormUrlEncoded
    @POST("info/themedetail")
    suspend fun getThemeDetail(
        @Field("theme_id") themeId : String
    ) : ThemeResponse

    @FormUrlEncoded
    @POST("map")
    suspend fun getPoints(
            @Body body: MapRequestBody
    ): MapResponse

    @FormUrlEncoded
    @POST("info/characterResource")
    suspend fun getCharacterUriList(
        @Field("character_id") characterType : String
    ) : CharacterUriResponse
}