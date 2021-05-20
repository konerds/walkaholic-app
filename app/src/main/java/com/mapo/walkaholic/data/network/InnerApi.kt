package com.mapo.walkaholic.data.network

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

    @POST("info/characterItem")
    suspend fun getCharacterItem(
        @Field("id") id: String
    ): CharacterItemResponse

    @GET("user/{id}/pet/appearance")
    suspend fun getUserCharacter(
        @Path("id") id: String
    ): UserCharacterResponse

    @GET("info/themelist")
    suspend fun getThemeEnum(): ThemeEnumResponse

    @FormUrlEncoded
    @POST("info/themedetail")
    suspend fun getThemeDetail(
        @Field("theme_id") themeId: String
    ): ThemeResponse

    @FormUrlEncoded
    @POST("map")
    suspend fun getPoints(
        @Body body: MapRequestBody
    ): MapResponse

    @FormUrlEncoded
    @POST("info/characterResource")
    suspend fun getCharacterUriList(
        @Field("character_id") characterType: String
    ): CharacterUriResponse

    @FormUrlEncoded
    @POST("info/calendarDate")
    suspend fun getCalendarDate(
        @Field("user_id") userId: Long,
        @Field("walk_date") walkDate: String
    ): WalkRecordResponse

    @FormUrlEncoded
    @POST("info/calendarMonth")
    suspend fun getCalendarMonth(
        @Field("user_id") userId: Long,
        @Field("walk_month") walkMonth: String
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