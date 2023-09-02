package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.BuildConfig
import com.mapo.walkaholic.data.model.response.*
import retrofit2.http.*

interface SgisApi {
    @GET("auth/authentication.json")
    suspend fun getAccessTokenSGIS(
            @Query("consumer_key") consumerKey: String,
            @Query("consumer_secret") consumerSecret: String
    ): SgisAccessTokenResponse

    @GET("transformation/transcoord.json")
    suspend fun getTmCoord(
            @Query("accessToken") accessToken: String,
            @Query("src") src: String,
            @Query("dst") dst: String,
            @Query("posX") posX: String,
            @Query("posY") posY: String
    ): TmCoordResponse
}