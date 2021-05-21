package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.AuthResponse
import com.mapo.walkaholic.data.model.response.FilenameLogoImageResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}

    private val _filenameLogoImageResponse : MutableLiveData<Resource<FilenameLogoImageResponse>> = MutableLiveData()
    val filenameLogoImageResponse: LiveData<Resource<FilenameLogoImageResponse>>
        get() = _filenameLogoImageResponse

    private val _loginResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<AuthResponse>>
        get() = _loginResponse

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

    fun getFilenameTitleLogo() = viewModelScope.launch {
        _filenameLogoImageResponse.value = repository.getFilenameLogoImage()
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

    /*
    fun login(token: OAuthToken) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _loginResponse.value = repository.login(token)
            progressBarVisibility.set(false)
        }
    }
     */
}