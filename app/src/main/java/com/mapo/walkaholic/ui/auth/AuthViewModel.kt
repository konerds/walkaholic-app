package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.*
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.model.response.StringResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.launch

class AuthViewModel(
        private val repository: AuthRepository
) : BaseViewModel() {
    override fun init() { }

    private val _loginResponse: MutableLiveData<Resource<StringResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<StringResponse>>
        get() = _loginResponse

    private val _registerResponse: MutableLiveData<Resource<StringResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<StringResponse>>
        get() = _registerResponse

    fun getAuth(callback: (OAuthToken?, Throwable?) -> Unit) = viewModelScope.launch {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(GlobalApplication.getGlobalApplicationContext())) {
            UserApiClient.instance.loginWithKakaoTalk(GlobalApplication.getGlobalApplicationContext(), callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(GlobalApplication.getGlobalApplicationContext(), callback = callback)
        }
    }

    fun login() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else _loginResponse.value = tokenInfo?.id?.let { repository.login(it) }
            }
        }
    }

    fun register(
            id: Long,
            name: String,
            nickname: String,
            birth: Int,
            gender: Int,
            height: Int,
            weight: Int
    ) = viewModelScope.launch {
        _registerResponse.value =
                repository.register(id, name, nickname, birth, gender, height, weight)
    }

    fun saveAuthToken() = viewModelScope.launch {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else tokenInfo?.id?.let { repository.saveAuthToken(it) }
            }
        }
    }
}