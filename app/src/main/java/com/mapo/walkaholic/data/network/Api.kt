package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.model.response.MapResponse
import com.mapo.walkaholic.data.model.response.StringResponse
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

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
            @Field("id") id: Long
    ): StringResponse

    @FormUrlEncoded
    @POST("auth/user")
    suspend fun getUser(
            @Field("id") id: Long
    ): UserResponse

    @FormUrlEncoded
    @POST("map")
    suspend fun getPoints(
            @Body body: MapRequestBody
    ): MapResponse
}