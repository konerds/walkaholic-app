package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.InnerApi

class SplashRepository(
    private val api: InnerApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    companion object {
        @Volatile
        private var instance: SplashRepository? = null

        @JvmStatic
        fun getInstance(
            api: InnerApi,
            preferences: UserPreferences
        ): SplashRepository =
            instance ?: synchronized(this) {
                instance ?: SplashRepository(api, preferences).also {
                    instance = it
                }
            }

    }
}