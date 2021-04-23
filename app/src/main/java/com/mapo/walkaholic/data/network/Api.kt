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

    @GET("B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty")
    suspend fun getWeatherDust(
            @Query("serviceKey") serviceKey: String,
            @Query("returnType") returnType: String,
            @Query("sidoName") sidoName: String,
            @Query("ver") ver: String
    ): WeatherDustResponse

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

    @GET("B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList")
    suspend fun getNearMsrstn(
            @Query("serviceKey") serviceKey: String,
            @Query("returnType") returnType: String,
            @Query("tmX") tmX: String,
            @Query("tmY") tmY: String
    ): NearMsrstnResponse

    @FormUrlEncoded
    @POST("map")
    suspend fun getPoints(
            @Body body: MapRequestBody
    ): MapResponse
}