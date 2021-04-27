package com.mapo.walkaholic.data.repository

import android.content.ContentValues
import android.util.Log
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.APISApi
import com.mapo.walkaholic.data.network.SGISApi
import com.mapo.walkaholic.data.network.InnerApi
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class DashboardRepository(
    private val api: InnerApi,
    private val apiWeather: APISApi,
    private val SGISApiSGIS: SGISApi,
    preferences: UserPreferences
) : BaseRepository(preferences) {
    companion object {
        private const val APIS_API_KEY =
            "qJr%2BQI4XC6oql7dTNz2MAuqL%2BKyg2AEdr6pKt2bBbzm9Bsj9jXkbPR%2FiQq%2BHKXN90xmsL%2BLrN4woIelJo1Ul4g%3D%3D"
        private const val APIS_WEATHER_NUM_OF_ROWS = "100"
        private const val APIS_RETURN_TYPE = "json"
        private const val APIS_ENCODE_TYPE = "utf-8"
        private const val APIS_WEATHER_DUST_VER = "1.3"
        private const val APIS_WEATHER_DATE_FORMAT = "yyyyMMdd"
        private const val APIS_WEATHER_TIME_FORMAT = "HHmm"
        private const val TIME_ZONE = "Asia/Seoul"
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
        apiWeather.getWeatherDust(URLDecoder.decode(APIS_API_KEY, APIS_ENCODE_TYPE), APIS_RETURN_TYPE, sidoName, APIS_WEATHER_DUST_VER)
    }

    suspend fun getSGISAccessToken() = safeApiCall {
        SGISApiSGIS.getAccessTokenSGIS(
            URLDecoder.decode(SGIS_API_CONSUMER_KEY),
            URLDecoder.decode(SGIS_API_SECRET_KEY)
        )
    }

    suspend fun getTmCoord(accessToken: String, currentX: String, currentY: String) = safeApiCall {
        SGISApiSGIS.getTmCoord(accessToken, SGIS_EPSG_WGS, SGIS_EPSG_BESSEL, currentX, currentY)
    }

    suspend fun getNearMsrstn(currentTmX: String, currentTmY: String) = safeApiCall {
        apiWeather.getNearMsrstn(
            URLDecoder.decode(APIS_API_KEY, APIS_ENCODE_TYPE),
            APIS_RETURN_TYPE,
            currentTmX,
            currentTmY
        )
    }

    suspend fun getWeather(dX: String, dY: String, requireDate : Date) = safeApiCall {
        val dateFormat = SimpleDateFormat(APIS_WEATHER_DATE_FORMAT, Locale.KOREAN)
        val currentTimeZone = TimeZone.getTimeZone(TIME_ZONE)
        val timeFormat = SimpleDateFormat(APIS_WEATHER_TIME_FORMAT)
        timeFormat.timeZone = currentTimeZone
        requireDate.hours -= 1
        apiWeather.getWeather(APIS_API_KEY, APIS_RETURN_TYPE, dateFormat.format(requireDate), timeFormat.format(requireDate), APIS_WEATHER_NUM_OF_ROWS, dX, dY)
    }
}