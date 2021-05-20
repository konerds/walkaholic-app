package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.AuthResponse
import com.mapo.walkaholic.data.model.response.FilenameLogoImageResponse
import com.mapo.walkaholic.data.model.response.TermPrivacyResponse
import com.mapo.walkaholic.data.model.response.TermServiceResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}

    private val _filenameLogoImageResponse : MutableLiveData<Resource<FilenameLogoImageResponse>> = MutableLiveData()
    val filenameLogoImageResponse: LiveData<Resource<FilenameLogoImageResponse>>
        get() = _filenameLogoImageResponse

    private val _termServiceResponse: MutableLiveData<Resource<TermServiceResponse>> = MutableLiveData()
    val termServiceResponse: LiveData<Resource<TermServiceResponse>>
        get() = _termServiceResponse

    private val _termPrivacyResponse: MutableLiveData<Resource<TermPrivacyResponse>> = MutableLiveData()
    val termPrivacyResponse: LiveData<Resource<TermPrivacyResponse>>
        get() = _termPrivacyResponse

    private val _registerResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<AuthResponse>>
        get() = _registerResponse

    fun getFilenameTitleLogo() = viewModelScope.launch {
        _filenameLogoImageResponse.value = repository.getFilenameLogoImage()
    }

    fun getTermService() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _termServiceResponse.value = repository.getTermService()
            progressBarVisibility.set(false)
        }
    }

    fun getTermPrivacy() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _termPrivacyResponse.value = repository.getTermPrivacy()
            progressBarVisibility.set(false)
        }
    }

    fun register(
        userBirth : String,
        userGender : String,
        userHeight : String,
        userNickname : String,
        userWeight : String
    ) {
        progressBarVisibility.set(true)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            tokenInfo!!.id.let {
                viewModelScope.launch {
                    _registerResponse.value =
                        repository.register(
                            userBirth,
                            userGender,
                            userHeight,
                            it,
                            userNickname,
                            userWeight
                        )
                }
            }
        }
        progressBarVisibility.set(false)
    }
}