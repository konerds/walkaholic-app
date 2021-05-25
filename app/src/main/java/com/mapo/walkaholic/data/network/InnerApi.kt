package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.data.model.request.BuyItemRequestBody
import com.mapo.walkaholic.data.model.request.EquipItemRequestBody
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface InnerApi {
    @GET("auth/logout")
    suspend fun logout(): ResponseBody

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): UserResponse

    @GET("user/{id}/exp")
    suspend fun getExpInformation(
        @Path("id") id: String
    ): ExpInformationResponse

    @GET("user/{id}/pet/appearance")
    suspend fun getUserCharacterFilename(
        @Path("id") id: String
    ): UserCharacterFilenameResponse

    @GET("user/{id}/item/equip")
    suspend fun getUserCharacterEquipStatus(
        @Path("id") id: String
    ): UserCharacterEquipStatusResponse

    @GET("user/{id}/item/view")
    suspend fun getUserCharacterPreviewFilename(
        @Path("id") id: String,
        @Query("faceItemId") faceItemId: String?,
        @Query("headItemId") headItemId: String?
    ): UserCharacterPreviewFilenameResponse

    @GET("global/resource/weather/{code}")
    suspend fun getFilenameWeather(
        @Path("code") code: String
    ): FilenameWeatherResponse

    @GET("global/resource/theme/name")
    suspend fun getCategoryTheme(): CategoryThemeResponse

    @GET("global/resource/theme")
    suspend fun getFilenameThemeCategoryImage(): FilenameThemeCategoryImageResponse

    @GET("course/theme")
    suspend fun getTheme(
        @Query("themeCode") themeCode: String
    ): ThemeResponse

    @GET("user/{id}/item")
    suspend fun getStatusUserCharacterInventoryItem(
        @Path("id") id: String
    ): UserInventoryItemStatusResponse

    @GET("item")
    suspend fun getStatusShopSaleItem(): ShopSaleItemStatusResponse

    @Headers(
        "Accept:application/json, text/plain, */*",
        "Content-Type:application/json;charset=UTF-8"
    )
    @POST("user/{id}/item")
    suspend fun buyItem(
        @Path("id") id: String,
        @Body buyItemInfo: BuyItemRequestBody
    ): BuyItemResponse

    @Headers(
        "Accept:application/json, text/plain, */*",
        "Content-Type:application/json;charset=UTF-8"
    )
    @PUT("user/{id}/item")
    suspend fun equipItem(
        @Path("id") id: String,
        @Body equipItemInfo: EquipItemRequestBody
    ): EquipItemResponse

    @DELETE("user/{id}/item/{itemId}")
    suspend fun deleteItem(
        @Path("id") id: String,
        @Path("itemId") itemId: String
    ): DeleteItemResponse

    @FormUrlEncoded
    @POST("map")
    suspend fun getPoints(
        @Body body: MapRequestBody
    ): MapResponse

    @GET("course/theme/{id}")
    suspend fun getThemeCourse(
        @Path("id") id: String
    ): ThemeCourseResponse

    @GET("user/{id}/detail/walk-record")
    suspend fun getWalkRecord(
        @Path("id") id: String,
        @Query("date") date: String
    ): WalkRecordResponse

    @GET("user/{id}/walk-record")
    suspend fun getCalendarMonth(
        @Path("id") id: Long,
        @Query("date") date: String
    ): WalkRecordExistInMonthResponse

    @FormUrlEncoded
    @POST("info/missionCondition")
    suspend fun getMissionCondition(
        @Field("mission_id") missionId: String,
    ): MissionConditionResponse

    @FormUrlEncoded
    @POST("info/missionProgress")
    suspend fun getMissionProgress(
        @Field("mission_id") missionId: String,
        @Field("condition_id") conditionId: String,
    ): MissionProgressResponse

    @FormUrlEncoded
    @POST("info/ranking")
    suspend fun getRanking(
        @Field("ranking_id") ranking_id: String
    ): RankingResponse

    @FormUrlEncoded
    @POST("info/favoritePath")
    suspend fun getFavoritePath(
        @Field("user_id") user_id: Long,
        @Field("id") id: String
    ): FavoritePathResponse

    /*@FormUrlEncoded
    @POST("info/missionDaily")
    suspend fun getMissionDaily(
        @Field("mission_id") missionId: String,
    ): MissionDailyResponse

    @FormUrlEncoded
    @POST("info/missionWeekly")
    suspend fun getMissionWeekly(
        @Field("mission_id") missionId: String,
    ): MissionWeeklyResponse*/
}