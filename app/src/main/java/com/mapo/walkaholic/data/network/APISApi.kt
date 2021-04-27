package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.response.NearMsrstnResponse
import com.mapo.walkaholic.data.model.response.WeatherDustResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface APISApi {
    @GET("B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty")
    suspend fun getWeatherDust(
            @Query("serviceKey") serviceKey: String,
            @Query("returnType") returnType: String,
            @Query("sidoName") sidoName: String,
            @Query("ver") ver: String
    ): WeatherDustResponse

    @GET("B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList")
    suspend fun getNearMsrstn(
            @Query("serviceKey") serviceKey: String,
            @Query("returnType") returnType: String,
            @Query("tmX") tmX: String,
            @Query("tmY") tmY: String
    ): NearMsrstnResponse


}