package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.InnerApi

class GuideRepository(
    private val api: InnerApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    companion object {
        @Volatile
        private var instance: GuideRepository? = null

        @JvmStatic
        fun getInstance(
            api: InnerApi,
            preferences: UserPreferences
        ): GuideRepository =
            instance ?: synchronized(this) {
                instance ?: GuideRepository(api, preferences).also {
                    instance = it
                }
            }

    }
}