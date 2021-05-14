package com.mapo.walkaholic.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.User
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
    private val _characterItemResponse: MutableLiveData<Resource<CharacterItemResponse>> = MutableLiveData()
    val characterItemResponse: LiveData<Resource<CharacterItemResponse>>
        get() = _characterItemResponse
    private val _userCharacterResponse: MutableLiveData<Resource<UserCharacterResponse>> = MutableLiveData()
    val userCharacterResponse: LiveData<Resource<UserCharacterResponse>>
        get() = _userCharacterResponse
    private val _expTableResponse: MutableLiveData<Resource<ExpTableResponse>> = MutableLiveData()
    val expTableResponse: LiveData<Resource<ExpTableResponse>>
        get() = _expTableResponse
    private val _weatherDustResponse: MutableLiveData<Resource<WeatherDustResponse>> = MutableLiveData()
    val weatherDustResponse: LiveData<Resource<WeatherDustResponse>>
        get() = _weatherDustResponse
    private val _sgisAccessTokenResponse: MutableLiveData<Resource<SgisAccessTokenResponse>> = MutableLiveData()
    val sgisAccessTokenResponse: LiveData<Resource<SgisAccessTokenResponse>>
        get() = _sgisAccessTokenResponse
    private val _tmCoordResponse: MutableLiveData<Resource<TmCoordResponse>> = MutableLiveData()
    val tmCoordResponse: LiveData<Resource<TmCoordResponse>>
        get() = _tmCoordResponse
    private val _nearMsrstnResponse: MutableLiveData<Resource<NearMsrstnResponse>> = MutableLiveData()
    val nearMsrstnResponse: LiveData<Resource<NearMsrstnResponse>>
        get() = _nearMsrstnResponse
    private val _todayWeatherResponse: MutableLiveData<Resource<TodayWeatherResponse>> = MutableLiveData()
    val todayWeatherResponse: LiveData<Resource<TodayWeatherResponse>>
        get() = _todayWeatherResponse
    private val _yesterdayWeatherResponse: MutableLiveData<Resource<YesterdayWeatherResponse>> = MutableLiveData()
    val yesterdayWeatherResponse: LiveData<Resource<YesterdayWeatherResponse>>
        get() = _yesterdayWeatherResponse
    private val _themeEnumResponse: MutableLiveData<Resource<ThemeEnumResponse>> = MutableLiveData()
    val themeEnumResponse: LiveData<Resource<ThemeEnumResponse>>
        get() = _themeEnumResponse
    private val _characterUriList: MutableLiveData<Resource<CharacterUriResponse>> = MutableLiveData()
    val characterUriList: LiveData<Resource<CharacterUriResponse>>
        get() = _characterUriList

    fun getDash() {
        progressBarVisibility.set(true)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else {
                    _userResponse.value = tokenInfo?.id?.let { mainRepository.getUser(it) }
                    _userCharacterResponse.value = tokenInfo?.id?.let { mainRepository.getUserCharacter(it) }
                }
                progressBarVisibility.set(false)
            }
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()

    fun getExpTable(exp: Long) {
        viewModelScope.launch {
            _expTableResponse.value = mainRepository.getExpTable(exp)
        }
    }

    fun showTodayString() : String {
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

    fun getTodayWeather(nX : String, nY : String) {
        viewModelScope.launch {
            val todayDate = Date()
            todayDate.hours -= 1
            _todayWeatherResponse.value = mainRepository.getTodayWeather(nX, nY, todayDate)
        }
    }

    fun getYesterdayWeather(nX : String, nY : String) {
        viewModelScope.launch {
            val yesterdayDate = Date()
            yesterdayDate.date -= 1
            yesterdayDate.hours += 1
            _yesterdayWeatherResponse.value = mainRepository.getYesterdayWeather(nX, nY, yesterdayDate)
        }
    }

    fun getUserCharacterItem(id: String) {
        viewModelScope.launch {
            _characterItemResponse.value = mainRepository.getCharacterItem(id)
        }
    }

    fun getDifferenceTemperature(todayTemperature : String?, yesterdayTemperature : String?) : String {
        if (todayTemperature.isNullOrEmpty() || yesterdayTemperature.isNullOrEmpty()) {
            return "오류"
        } else {
            val differenceTemperature = (todayTemperature.toInt() - yesterdayTemperature.toInt())
            if (differenceTemperature > 0) {
                return "어제보다 ${differenceTemperature.absoluteValue}도 높아요"
            } else if (differenceTemperature == 0) {
                return "어제같은 날씨에요"
            } else if (differenceTemperature < 0) {
                return "어제보다 ${differenceTemperature.absoluteValue}도 낮아요"
            } else {
                return "오류"
            }
        }
    }

    fun getThemeEnum() {
        viewModelScope.launch {
            _themeEnumResponse.value = mainRepository.getThemeEnum()
        }
    }

    fun getCharacterUriList(characterType: String) {
        viewModelScope.launch {
            _characterUriList.value = mainRepository.getCharacterUriList(characterType)
        }
    }
}