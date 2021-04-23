package com.mapo.walkaholic.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
        private val repository: DashboardRepository
) : BaseViewModel() {
    override fun init() {}
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _userCharacterResponse: MutableLiveData<Resource<UserCharacterResponse>> = MutableLiveData()
    val userCharacterResponse: LiveData<Resource<UserCharacterResponse>>
        get() = _userCharacterResponse
    private val _expTableResponse: MutableLiveData<Resource<ExpTableResponse>> = MutableLiveData()
    val expTableResponse: LiveData<Resource<ExpTableResponse>>
        get() = _expTableResponse
    private val _weatherDustResponse: MutableLiveData<Resource<WeatherDustResponse>> = MutableLiveData()
    val weatherDustResponse: LiveData<Resource<WeatherDustResponse>>
        get() = _weatherDustResponse
    private val _sgisAccessTokenResponse: MutableLiveData<Resource<SGISAccessTokenResponse>> = MutableLiveData()
    val sgisAccessTokenResponse: LiveData<Resource<SGISAccessTokenResponse>>
        get() = _sgisAccessTokenResponse
    private val _tmCoordResponse: MutableLiveData<Resource<TmCoordResponse>> = MutableLiveData()
    val tmCoordResponse: LiveData<Resource<TmCoordResponse>>
        get() = _tmCoordResponse
    private val _nearMsrstnResponse: MutableLiveData<Resource<NearMsrstnResponse>> = MutableLiveData()
    val nearMsrstnResponse: LiveData<Resource<NearMsrstnResponse>>
        get() = _nearMsrstnResponse

    fun getDash() {
        progressBarVisibility.set(true)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else {
                    _userResponse.value = tokenInfo?.id?.let { repository.getUser(it) }
                    _userCharacterResponse.value = tokenInfo?.id?.let { repository.getUserCharacter(it) }
                }
                progressBarVisibility.set(false)
            }
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()

    fun getExpTable(exp: Long) {
        viewModelScope.launch {
            _expTableResponse.value = repository.getExpTable(exp)
        }
    }

    fun showTodayString() : String {
        val format = SimpleDateFormat("yyyy년 MM월 dd일, E요일", Locale.KOREAN)
        return format.format(Date())
    }

    fun getWeatherDust(sidoName: String) {
        viewModelScope.launch {
            _weatherDustResponse.value = repository.getWeatherDust(sidoName)
        }
    }

    fun getSGISAccessToken() {
        viewModelScope.launch {
            _sgisAccessTokenResponse.value = repository.getSGISAccessToken()
        }
    }

    fun getTmCoord(accessToken: String, currentX: String, currentY: String) {
        viewModelScope.launch {
            _tmCoordResponse.value = repository.getTmCoord(accessToken, currentX, currentY)
        }
    }

    fun getNearMsrstn(currentTmX: String, currentTmY: String) {
        viewModelScope.launch {
            _nearMsrstnResponse.value = repository.getNearMsrstn(currentTmX, currentTmY)
        }
    }

    fun getUserCharacterName(type: Int) =
            when (type) {
                0 -> "주황 파뿌리"
                1 -> "파랑 파뿌리"
                2 -> "노랑 파뿌리"
                else -> "[오류]"
            }
}