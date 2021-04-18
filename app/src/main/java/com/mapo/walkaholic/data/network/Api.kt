package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.*
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("service")
    suspend fun anyService(): Any

    @FormUrlEncoded
    @POST("auth/create")
    suspend fun register(
            @Field("id") id: Long,
            @Field("name") name: String,
            @Field("nickname") nickname: String,
            @Field("birth") birth: Int,
            @Field("gender") gender: Int,
            @Field("height") height: Int,
            @Field("weight") weight: Int
    ): StringResponse

    @GET("auth/term")
    suspend fun getTerm(): TermResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
            @Field("id") id: Long
    ): StringResponse

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

    @FormUrlEncoded
    @POST("map")
    suspend fun getPoints(
            @Body body: MapRequestBody
    ): MapResponse
}