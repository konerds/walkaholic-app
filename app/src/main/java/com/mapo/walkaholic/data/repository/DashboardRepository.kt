package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.network.Api
import java.net.URLDecoder

class DashboardRepository(
        private val api: Api,
        private val apiWeather: Api,
        private val apiSGIS: Api
) : BaseRepository() {
    companion object {
        private const val APIS_API_KEY = "FUWeMQdbeMsoFdcKoD5uQYnSTPeDxHgzXrdbaIL9hJXZ3z5TC2RUkAVhQX56rNgcn6o9qhxiC0KDNa8EWYh19A%3D%3D"
        private const val SGIS_API_CONSUMER_KEY = "5a0b5bf4bb2f42daac3d"
        private const val SGIS_API_SECRET_KEY = "70836f6824bb4bb88335"
        private const val SGIS_EPSG_WGS = "4326"
        private const val SGIS_EPSG_BESSEL = "5181"
    }

    suspend fun getUser(id: Long) = safeApiCall {
        api.getUser(id)
    }

    suspend fun getUserCharacter(id: Long) = safeApiCall {
        api.getCharacter(id)
    }

    suspend fun getExpTable(exp: Long) = safeApiCall {
        api.getExpTable(exp)
    }

    suspend fun getWeatherDust(sidoName: String) = safeApiCall {
        apiWeather.getWeatherDust(URLDecoder.decode(APIS_API_KEY, "utf-8"), "json", sidoName, "1.3")
    }

    suspend fun getSGISAccessToken() = safeApiCall {
        apiSGIS.getAccessTokenSGIS(URLDecoder.decode(SGIS_API_CONSUMER_KEY), URLDecoder.decode(SGIS_API_SECRET_KEY))
    }

    suspend fun getTmCoord(accessToken: String, currentX: String, currentY: String) = safeApiCall {
        apiSGIS.getTmCoord(accessToken, SGIS_EPSG_WGS, SGIS_EPSG_BESSEL, currentX, currentY)
    }

    suspend fun getNearMsrstn(currentTmX: String, currentTmY: String) = safeApiCall {
        apiWeather.getNearMsrstn(URLDecoder.decode(APIS_API_KEY, "utf-8"), "json", currentTmX, currentTmY)
    }
}