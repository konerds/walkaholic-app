package com.mapo.walkaholic.data.repository

import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.request.BuyItemRequestBody
import com.mapo.walkaholic.data.model.request.EquipItemRequestBody
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.request.WalkRewardRequestBody
import com.mapo.walkaholic.data.model.response.TodayWeatherResponse
import com.mapo.walkaholic.data.model.response.YesterdayWeatherResponse
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
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
        private const val SGIS_API_CONSUMER_KEY = "70ae3222d3e94dd5a9e5"
        private const val SGIS_API_SECRET_KEY = "638a9b5ec41b40f2ba5a"
        private const val SGIS_EPSG_WGS = "4326"
        private const val SGIS_EPSG_BESSEL = "5181"
    }

    private var userId : String = ""

    suspend fun getUser() = safeApiCall {
        //setUser()
        //api.getUser(userId)

        api.getUser("1693276776")
    }

    private fun setUser() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                // Fail
            }
            else if (tokenInfo != null) {
                // Success
                userId = tokenInfo.id.toString()
            }
        }
    }

    suspend fun getUserCharacterFilename(userId: Long) = safeApiCall {
        api.getUserCharacterFilename(userId.toString())
    }

    suspend fun getUserCharacterEquipStatus(userId: Long) = safeApiCall {
        api.getUserCharacterEquipStatus(userId.toString())
    }

    suspend fun getUserCharacterPreviewFilename(
        userId: Long,
        faceItemId: String,
        headItemId: String
    ) = safeApiCall {
        api.getUserCharacterPreviewFilename(
            userId.toString(),
            if (faceItemId.isEmpty()) {
                null
            } else {
                faceItemId
            }, if (headItemId.isEmpty()) {
                null
            } else {
                headItemId
            }
        )
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

    suspend fun getFilenameWeather(weatherCode: String) = safeApiCall {
        api.getFilenameWeather(weatherCode)
    }

    suspend fun getFilenameThemeCategoryImage() = safeApiCall {
        api.getFilenameThemeCategoryImage()
    }

    suspend fun getCategoryTheme() = safeApiCall {
        api.getCategoryTheme()
    }

    suspend fun getStatusUserCharacterInventoryItem(userId: Long) = safeApiCall {
        api.getStatusUserCharacterInventoryItem(userId.toString())
    }

    suspend fun getStatusShopSaleItem() = safeApiCall {
        api.getStatusShopSaleItem()
    }

    suspend fun buyItem(userId: Long, arrayListItemId: ArrayList<Int?>) = safeApiCall {
        api.buyItem(
            userId.toString(), BuyItemRequestBody(
                arrayListItemId
            )
        )
    }

    suspend fun equipItem(userId: Long, faceItemId: Int?, hairItemId: Int?) = safeApiCall {
        api.equipItem(
            userId.toString(),
            EquipItemRequestBody(faceItemId ?: 0, hairItemId ?: 0)
        )
    }

    suspend fun deleteItem(userId: Long, itemId: String) = safeApiCall {
        api.deleteItem(userId.toString(), itemId)
    }

    suspend fun getWalkRecord(userId: Long, walkDate: String) = safeApiCall {
        api.getWalkRecord(userId.toString(), walkDate)
    }

    suspend fun getCalendarMonth(userId: Long, walkMonth: String) = safeApiCall {
        api.getCalendarMonth(userId, walkMonth)
    }

    suspend fun getTheme(position: Int) = safeApiCall {
        api.getTheme(
            when (position) {
                0 -> "001"
                1 -> "002"
                2 -> "003"
                else -> ""
            }
        )
    }

    suspend fun getMission(userId: Long, missionType: Int) = safeApiCall {
        api.getMission(userId.toString(), missionType.toString())
    }

    suspend fun getMissionReward(userId: Long, missionId: String) = safeApiCall {
        api.getMissionReward(userId.toString(), missionId)
    }

    suspend fun getThemeCourse(id: Int) = safeApiCall {
        api.getThemeCourse(id.toString())
    }

    suspend fun getThemeCourseRoute(id: Int) = safeApiCall {
        api.getThemeCourseRoute(id.toString())
    }

    suspend fun getRanking(position: Int) = safeApiCall {
        api.getRanking(position)
    }

    suspend fun getMonthRanking(userId: Long) = safeApiCall {
        api.getMonthRanking(userId.toString())
    }

    suspend fun getAccumulateRanking(userId: Long) = safeApiCall {
        api.getAccumulateRanking(userId.toString())
    }

    suspend fun getPoints(@Body body: MapRequestBody) = safeApiCall {
        api.getPoints(body)
    }

    suspend fun getFavoritePath(userId: Long, position: Int) = safeApiCall {
        api.getFavoritePath(
            userId, when (position) {
                0 -> "00"
                1 -> "01"
                else -> ""
            }
        )
    }

    suspend fun getMarker(type:Int, latitude: String, longitude: String) = safeApiCall {
        api.getMarker(type.toString(), latitude, longitude)
    }

    suspend fun setReward(userId: Long, walkCount : Int) = safeApiCall {
        api.setReward(userId.toString(), WalkRewardRequestBody(walkCount.toLong()))
    }

}