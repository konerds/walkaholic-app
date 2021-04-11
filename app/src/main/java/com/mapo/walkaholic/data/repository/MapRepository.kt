package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.network.Api
import retrofit2.http.Body

class MapRepository(
    private val api: Api,
    private val preferences: UserPreferences
) : BaseRepository() {
    suspend fun getPoints(@Body body: MapRequestBody) = safeApiCall {
        api.getPoints(body)
    }

    suspend fun saveAuthToken(id: Long) {
        preferences.saveAuthToken(id)
    }
}