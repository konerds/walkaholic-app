package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi

class GuideRepository(
    private val api: GuestApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    companion object {
        @Volatile
        private var instance: GuideRepository? = null

        @JvmStatic
        fun getInstance(
            api: GuestApi,
            preferences: UserPreferences
        ): GuideRepository =
            instance ?: synchronized(this) {
                instance ?: GuideRepository(api, preferences).also {
                    instance = it
                }
            }

    }
}