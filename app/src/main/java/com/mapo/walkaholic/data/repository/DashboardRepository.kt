package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.network.Api

class DashboardRepository(
        private val api: Api,
        private val apiWeather: Api
) : BaseRepository() {
    suspend fun getUser(id:Long) = safeApiCall {
        api.getUser(id)
    }
    suspend fun getUserCharacter(id:Long) = safeApiCall {
        api.getCharacter(id)
    }
    suspend fun getExpTable(exp:Long) = safeApiCall {
        api.getExpTable(exp)
    }
}