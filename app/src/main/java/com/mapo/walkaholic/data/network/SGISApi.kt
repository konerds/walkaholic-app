package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.*
import retrofit2.http.*

interface SGISApi {
    @GET("auth/authentication.json")
    suspend fun getAccessTokenSGIS(
            @Query("consumer_key") consumerKey: String,
            @Query("consumer_secret") consumerSecret: String
    ): SGISAccessTokenResponse

    @GET("transformation/transcoord.json")
    suspend fun getTmCoord(
            @Query("accessToken") accessToken: String,
            @Query("src") src: String,
            @Query("dst") dst: String,
            @Query("posX") posX: String,
            @Query("posY") posY: String
    ): TmCoordResponse
}