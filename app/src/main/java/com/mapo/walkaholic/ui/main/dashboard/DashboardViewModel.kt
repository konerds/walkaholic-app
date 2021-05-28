package com.mapo.walkaholic.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class DashboardViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {}
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _expInformationResponse: MutableLiveData<Resource<ExpInformationResponse>> =
        MutableLiveData()
    val expInformationResponse: LiveData<Resource<ExpInformationResponse>>
        get() = _expInformationResponse
    private val _userCharacterFilenameResponse: MutableLiveData<Resource<UserCharacterFilenameResponse>> =
        MutableLiveData()
    val userCharacterFilenameResponse: LiveData<Resource<UserCharacterFilenameResponse>>
        get() = _userCharacterFilenameResponse
    private val _weatherDustResponse: MutableLiveData<Resource<WeatherDustResponse>> =
        MutableLiveData()
    val weatherDustResponse: LiveData<Resource<WeatherDustResponse>>
        get() = _weatherDustResponse
    private val _sgisAccessTokenResponse: MutableLiveData<Resource<SgisAccessTokenResponse>> =
        MutableLiveData()
    val sgisAccessTokenResponse: LiveData<Resource<SgisAccessTokenResponse>>
        get() = _sgisAccessTokenResponse
    private val _tmCoordResponse: MutableLiveData<Resource<TmCoordResponse>> = MutableLiveData()
    val tmCoordResponse: LiveData<Resource<TmCoordResponse>>
        get() = _tmCoordResponse
    private val _nearMsrstnResponse: MutableLiveData<Resource<NearMsrstnResponse>> =
        MutableLiveData()
    val nearMsrstnResponse: LiveData<Resource<NearMsrstnResponse>>
        get() = _nearMsrstnResponse
    private val _todayWeatherResponse: MutableLiveData<Resource<TodayWeatherResponse>> =
        MutableLiveData()
    val todayWeatherResponse: LiveData<Resource<TodayWeatherResponse>>
        get() = _todayWeatherResponse
    private val _yesterdayWeatherResponse: MutableLiveData<Resource<YesterdayWeatherResponse>> =
        MutableLiveData()
    val yesterdayWeatherResponse: LiveData<Resource<YesterdayWeatherResponse>>
        get() = _yesterdayWeatherResponse
    private val _filenameWeatherResponse: MutableLiveData<Resource<FilenameWeatherResponse>> =
        MutableLiveData()
    val filenameWeatherResponse: LiveData<Resource<FilenameWeatherResponse>>
        get() = _filenameWeatherResponse
    private val _filenameThemeCategoryImageResponse: MutableLiveData<Resource<FilenameThemeCategoryImageResponse>> =
        MutableLiveData()
    val filenameThemeCategoryImageResponse: LiveData<Resource<FilenameThemeCategoryImageResponse>>
        get() = _filenameThemeCategoryImageResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()

    fun getExpInformation(userId: Long) {
        viewModelScope.launch {
            _expInformationResponse.value = mainRepository.getExpInformation(userId)
        }
    }

    fun getUserCharacterFilename(userId: Long) {
        viewModelScope.launch {
            _userCharacterFilenameResponse.value = mainRepository.getUserCharacterFilename(userId)
        }
    }

    fun showTodayString(): String {
        val format = SimpleDateFormat("yyyy년 MM월 dd일, E요일", Locale.KOREAN)
        return format.format(Date())
    }

    fun getWeatherDust(sidoName: String) {
        viewModelScope.launch {
            _weatherDustResponse.value = mainRepository.getWeatherDust(sidoName)
        }
    }

    fun getSGISAccessToken() {
        viewModelScope.launch {
            _sgisAccessTokenResponse.value = mainRepository.getSGISAccessToken()
        }
    }

    fun getTmCoord(accessToken: String, currentX: String, currentY: String) {
        viewModelScope.launch {
            _tmCoordResponse.value = mainRepository.getTmCoord(accessToken, currentX, currentY)
        }
    }

    fun getNearMsrstn(currentTmX: String, currentTmY: String) {
        viewModelScope.launch {
            _nearMsrstnResponse.value = mainRepository.getNearMsrstn(currentTmX, currentTmY)
        }
    }

    fun getTodayWeather(nX: String, nY: String) {
        viewModelScope.launch {
            val todayDate = Date()
            todayDate.hours -= 1
            _todayWeatherResponse.value = mainRepository.getTodayWeather(nX, nY, todayDate)
        }
    }

    fun getYesterdayWeather(nX: String, nY: String) {
        viewModelScope.launch {
            val yesterdayDate = Date()
            yesterdayDate.date -= 1
            yesterdayDate.hours += 1
            _yesterdayWeatherResponse.value =
                mainRepository.getYesterdayWeather(nX, nY, yesterdayDate)
        }
    }

    fun getBurnCalorie(walkCount : Int) = (walkCount * 5).toString() + "kcal"

    fun getDifferenceTemperature(todayTemperature: String?, yesterdayTemperature: String?): String {
        if (todayTemperature.isNullOrEmpty() || yesterdayTemperature.isNullOrEmpty()) {
            return "오류"
        } else {
            val differenceTemperature = (todayTemperature.toInt() - yesterdayTemperature.toInt())
            if (differenceTemperature > 0) {
                return "어제보다 ${differenceTemperature.absoluteValue}º 높아요"
            } else if (differenceTemperature == 0) {
                return "어제같은 날씨에요"
            } else if (differenceTemperature < 0) {
                return "어제보다 ${differenceTemperature.absoluteValue}º 낮아요"
            } else {
                return "오류"
            }
        }
    }

    fun getFilenameWeather(weatherCode: String) {
        viewModelScope.launch {
            _filenameWeatherResponse.value = mainRepository.getFilenameWeather(weatherCode)
        }
    }

    fun getFilenameThemeCategoryImage() {
        viewModelScope.launch {
            _filenameThemeCategoryImageResponse.value =
                mainRepository.getFilenameThemeCategoryImage()
        }
    }
}