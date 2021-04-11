package com.mapo.walkaholic.data.network

import com.google.gson.JsonObject
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.LoginResponse
import com.mapo.walkaholic.data.model.response.MapResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("service")
    suspend fun anyService(): Any

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("id") id: Long
    ): LoginResponse

    @FormUrlEncoded
    @POST("current")
    suspend fun getPoints(@Body body: MapRequestBody) : MapResponse
}