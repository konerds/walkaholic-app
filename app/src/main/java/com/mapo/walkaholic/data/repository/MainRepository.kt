package com.mapo.walkaholic.data.repository

import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.TodayWeatherResponse
import com.mapo.walkaholic.data.model.response.YesterdayWeatherResponse
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.naver.maps.map.NaverMap
import retrofit2.http.Body
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class MainRepository(
    private val api: InnerApi,
    private val apiWeather: ApisApi,
    private val SGISApiSgis: SgisApi,
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
        private const val SGIS_API_CONSUMER_KEY = "67ad057e051144d2a09a"
        private const val SGIS_API_SECRET_KEY = "5b4ac1c4c89c4ad8bc41"
        private const val SGIS_EPSG_WGS = "4326"
        private const val SGIS_EPSG_BESSEL = "5181"
    }

    private var mMap: NaverMap? = null

    fun setNaverMap(mMap: NaverMap) {
        this.mMap = mMap
    }

    fun getNaverMap() = this.mMap

    suspend fun getUser(userId: Long) = safeApiCall {
        api.getUser(userId.toString())
    }

    suspend fun getUserCharacterFilename(userId: Long) = safeApiCall {
        api.getUserCharacterFilename(userId.toString())
    }

    suspend fun getUserCharacterEquipStatus(userId: Long) = safeApiCall {
        api.getUserCharacterEquipStatus(userId.toString())
    }

    suspend fun getUserCharacterPreviewFilename(faceItemId : String, headItemId : String, userId: Long) = safeApiCall {
        api.getUserCharacterPreviewFilename(faceItemId, headItemId, userId.toString())
    }

    suspend fun getCharacterItem(petId: Int) = safeApiCall {
        api.getCharacterItem(petId.toString())
    }

    suspend fun getExpInformation(userId: Long) = safeApiCall {
        api.getExpInformation(userId.toString())
    }

    suspend fun getWeatherDust(sidoName: String) = safeApiCall {
        apiWeather.getWeatherDust(
            URLDecoder.decode(APIS_API_KEY, APIS_ENCODE_TYPE),
            APIS_RETURN_TYPE,
            sidoName,
            APIS_WEATHER_DUST_VER
        )
    }

    suspend fun getSGISAccessToken() = safeApiCall {
        SGISApiSgis.getAccessTokenSGIS(
            URLDecoder.decode(SGIS_API_CONSUMER_KEY),
            URLDecoder.decode(SGIS_API_SECRET_KEY)
        )
    }

    suspend fun getTmCoord(accessToken: String, currentX: String, currentY: String) = safeApiCall {
        SGISApiSgis.getTmCoord(accessToken, SGIS_EPSG_WGS, SGIS_EPSG_BESSEL, currentX, currentY)
    }

    suspend fun getNearMsrstn(currentTmX: String, currentTmY: String) = safeApiCall {
        apiWeather.getNearMsrstn(
            URLDecoder.decode(APIS_API_KEY, APIS_ENCODE_TYPE),
            APIS_RETURN_TYPE,
            currentTmX,
            currentTmY
        )
    }

    suspend fun getTodayWeather(
        nX: String,
        nY: String,
        requireDate: Date
    ): Resource<TodayWeatherResponse> {
        val currentTimeZone = TimeZone.getTimeZone(TIME_ZONE)
        val dateFormat = SimpleDateFormat(APIS_WEATHER_DATE_FORMAT, Locale.KOREAN)
        val timeFormat = SimpleDateFormat(APIS_WEATHER_TIME_FORMAT, Locale.KOREAN)
        dateFormat.timeZone = currentTimeZone
        timeFormat.timeZone = currentTimeZone
        return safeApiCall {
            apiWeather.getTodayWeather(
                URLDecoder.decode(APIS_API_KEY, APIS_ENCODE_TYPE),
                APIS_RETURN_TYPE,
                dateFormat.format(requireDate),
                timeFormat.format(requireDate),
                APIS_WEATHER_NUM_OF_ROWS,
                nX,
                nY
            )
        }
    }

    suspend fun getYesterdayWeather(
        nX: String,
        nY: String,
        requireDate: Date
    ): Resource<YesterdayWeatherResponse> {
        val currentTimeZone = TimeZone.getTimeZone(TIME_ZONE)
        val dateFormat = SimpleDateFormat(APIS_WEATHER_DATE_FORMAT, Locale.KOREAN)
        val timeFormat = SimpleDateFormat(APIS_WEATHER_TIME_FORMAT, Locale.KOREAN)
        dateFormat.timeZone = currentTimeZone
        timeFormat.timeZone = currentTimeZone
        return safeApiCall {
            apiWeather.getYesterdayWeather(
                URLDecoder.decode(APIS_API_KEY, APIS_ENCODE_TYPE),
                APIS_RETURN_TYPE,
                dateFormat.format(requireDate),
                timeFormat.format(requireDate),
                APIS_WEATHER_NUM_OF_ROWS,
                nX,
                nY
            )
        }
    }

    suspend fun getFilenameThemeCategoryImage() = safeApiCall {
        api.getFilenameThemeCategoryImage()
    }

    suspend fun getThemeDetail(themeId: String) = safeApiCall {
        api.getThemeDetail(themeId)
    }

    suspend fun getCharacterUriList(characterType: String) = safeApiCall {
        api.getCharacterUriList(characterType)
    }

    suspend fun getCalendarDate(userId: Long, walkDate: String) = safeApiCall {
        api.getCalendarDate(userId, walkDate)
    }

    suspend fun getCalendarMonth(userId: Long, walkMonth: String) = safeApiCall {
        api.getCalendarMonth(userId, walkMonth)
    }

    suspend fun getPoints(@Body body: MapRequestBody) = safeApiCall {
        api.getPoints(body)
    }

    suspend fun getMissionCondition(missionID: String) = safeApiCall {
        api.getMissionCondition(missionID)
    }

    suspend fun getMissionProgress(missionID: String, conditionId: String) = safeApiCall {
        api.getMissionProgress(missionID, conditionId)
    }

    suspend fun getRanking(rankingId: String) = safeApiCall {
        api.getRanking(rankingId)
    }

    suspend fun getStoragePath(userId: Long) = safeApiCall {
        api.getStoragePath(userId)
    }


/*    suspend fun getMissionDaily(missionID:String) = safeApiCall {
        api.getMissionDaily(missionID)
    }

    suspend fun getMissionWeekly(missionID:String) = safeApiCall {
        api.getMissionWeekly(missionID)
    }*/
}