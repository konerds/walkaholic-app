package com.mapo.walkaholic.ui.auth

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.StringResponse
import com.mapo.walkaholic.data.model.response.TermResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}

    private val _termResponse: MutableLiveData<Resource<TermResponse>> = MutableLiveData()
    val termResponse: LiveData<Resource<TermResponse>>
        get() = _termResponse

    private val _loginResponse: MutableLiveData<Resource<StringResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<StringResponse>>
        get() = _loginResponse

    private val _registerResponse: MutableLiveData<Resource<StringResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<StringResponse>>
        get() = _registerResponse

    fun getAuth(callback: (OAuthToken?, Throwable?) -> Unit) = viewModelScope.launch {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(GlobalApplication.getGlobalApplicationContext())) {
            UserApiClient.instance.loginWithKakaoTalk(
                GlobalApplication.getGlobalApplicationContext(),
                callback = callback
            )
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                GlobalApplication.getGlobalApplicationContext(),
                callback = callback
            )
        }
    }

    fun getTerm() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _termResponse.value = repository.getTerm()
            progressBarVisibility.set(false)
        }
    }

    fun login() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                viewModelScope.launch {
                    if (error != null) {
                    } else {
                        _loginResponse.value = tokenInfo?.id?.let { repository.login(it) }
                    }
                    progressBarVisibility.set(false)
                }
            }
        }
    }

    fun register(
        nickname: String,
        birth: Int,
        gender: Int,
        height: Int,
        weight: Int
    ) {
        progressBarVisibility.set(true)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            tokenInfo!!.id.toString().trim().let {
                if (it != null) {
                    viewModelScope.launch {
                        _registerResponse.value =
                            repository.register(
                                it.toLong(),
                                nickname,
                                birth,
                                gender,
                                height,
                                weight
                            )
                    }
                } else {
                    logout()
                }
            }
        }
        progressBarVisibility.set(false)
    }
}