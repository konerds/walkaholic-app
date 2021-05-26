package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository(
    private val preferences: UserPreferences
) {
    companion object {
        private const val RESOURCE_BASE_URL = "http://15.164.103.223:8080/static/img/"
        private const val PIXELS_PER_METRE = 4
    }

    suspend fun <T> safeApiCall(
            apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }

    fun getPixelsPerMetre() = PIXELS_PER_METRE

    fun getResourceBaseUri() = RESOURCE_BASE_URL

    suspend fun saveAuthToken(accessToken: String) {
        preferences.saveJwtToken(accessToken)
    }

    suspend fun saveIsFirst(isFirst: Boolean) {
        preferences.saveIsFirst(isFirst)
    }

    suspend fun saveIsLocationGranted(isLocationGranted : Boolean) {
        preferences.saveIsLocationGranted(isLocationGranted)
    }
}