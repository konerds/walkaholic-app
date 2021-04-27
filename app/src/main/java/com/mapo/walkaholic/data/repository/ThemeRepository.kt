package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.InnerApi

class ThemeRepository(
    private val api: InnerApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    suspend fun anyService(
        par1: String,
        par2: String
    ) = safeApiCall { }
}