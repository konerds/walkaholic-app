package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.network.Api

class DashboardRepository(
        private val api: Api,
        private val apiWeather: Api
) : BaseRepository() {
    suspend fun getDash(id:Long) = safeApiCall {
        api.getDash(id)
    }
}