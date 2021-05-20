package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.AuthResponse
import com.mapo.walkaholic.data.model.response.TermResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}

    private val _filenameLogoTitle = MutableLiveData("logo_title.png")
    val filenameLogoTitle: LiveData<String>
        get() = _filenameLogoTitle

    private val _termResponse: MutableLiveData<Resource<TermResponse>> = MutableLiveData()
    val termResponse: LiveData<Resource<TermResponse>>
        get() = _termResponse

    private val _registerResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<AuthResponse>>
        get() = _registerResponse

    fun getFilenameTitleLogo() = viewModelScope.launch {
        _filenameLogoTitle.value = "logo_title.png"
    }

    fun getTerm() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _termResponse.value = repository.getTerm()
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