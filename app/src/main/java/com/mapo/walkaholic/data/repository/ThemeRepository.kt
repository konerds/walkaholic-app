package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Api

class ThemeRepository(
    private val api: Api
) : BaseRepository() {
    suspend fun anyService(
            par1: String,
            par2: String
    ) = safeApiCall {
        api.anyService()
    }
}