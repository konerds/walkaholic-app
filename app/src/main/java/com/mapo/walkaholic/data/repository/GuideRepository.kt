package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi

class GuideRepository(
    private val api: GuestApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    suspend fun getTutorialFilenames() = safeApiCall {
        api.getTutorialFilenames()
    }
}