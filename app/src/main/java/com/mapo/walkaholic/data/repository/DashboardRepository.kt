package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Api

class DashboardRepository(
        private val api: Api
) : BaseRepository() {
    suspend fun getUser(id:Long) = safeApiCall {
        api.getUser(id)
    }

}