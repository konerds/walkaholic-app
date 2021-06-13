package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.mapo.walkaholic.data.model.response.RegisterResponse
import com.mapo.walkaholic.data.model.response.FilenameLogoImageResponse
import com.mapo.walkaholic.data.model.response.LoginResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}

    private val _filenameLogoImageResponse : MutableLiveData<Resource<FilenameLogoImageResponse>> = MutableLiveData()
    val filenameLogoImageResponse: LiveData<Resource<FilenameLogoImageResponse>>
        get() = _filenameLogoImageResponse

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    fun getFilenameTitleLogo() = viewModelScope.launch {
        _filenameLogoImageResponse.value = repository.getFilenameLogoImage()
    }

//    fun login() {
//        progressBarVisibility.set(true)
//        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//            if (error != null) {
//                // Fail
//            }
//            else if (tokenInfo != null) {
//                // Success
//                viewModelScope.launch {
//                    _loginResponse.value = repository.login(tokenInfo.id)
//                    progressBarVisibility.set(false)
//                }
//            }
//        }
//    }

    fun login(token: OAuthToken) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _loginResponse.value = repository.login(token)
            progressBarVisibility.set(false)
        }
    }
}