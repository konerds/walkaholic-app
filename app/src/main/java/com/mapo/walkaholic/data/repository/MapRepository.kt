package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.network.InnerApi
import retrofit2.http.Body

class MapRepository(
    private val api: InnerApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    suspend fun getPoints(@Body body: MapRequestBody) = safeApiCall {
        api.getPoints(body)
    }
}