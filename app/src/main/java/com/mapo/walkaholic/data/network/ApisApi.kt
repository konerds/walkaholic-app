package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.response.NearMsrstnResponse
import com.mapo.walkaholic.data.model.response.WeatherDustResponse
import com.mapo.walkaholic.data.model.response.TodayWeatherResponse
import com.mapo.walkaholic.data.model.response.YesterdayWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApisApi {
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

    @GET("1360000/VilageFcstInfoService/getUltraSrtFcst")
    suspend fun getTodayWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("dataType") returnType: String,
        @Query("base_date") baseDate : String,
        @Query("base_time") baseTime : String,
        @Query("numOfRows") numOfRows : String,
        @Query("nx") nX : String,
        @Query("ny") nY : String
    ) : TodayWeatherResponse

    @GET("1360000/VilageFcstInfoService/getUltraSrtFcst")
    suspend fun getYesterdayWeather(
            @Query("serviceKey") serviceKey: String,
            @Query("dataType") returnType: String,
            @Query("base_date") baseDate : String,
            @Query("base_time") baseTime : String,
            @Query("numOfRows") numOfRows : String,
            @Query("nx") nX : String,
            @Query("ny") nY : String
    ) : YesterdayWeatherResponse
}