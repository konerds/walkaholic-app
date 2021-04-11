package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.LoginResponse
import com.mapo.walkaholic.data.model.response.MapResponse
import com.mapo.walkaholic.data.model.response.RegisterResponse
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
    ): RegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("id") id: Long
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/user")
    suspend fun getUser(
        @Field("id") id: Long
    ): LoginResponse

    @FormUrlEncoded
    @POST("current")
    suspend fun getPoints(@Body body: MapRequestBody): MapResponse
}