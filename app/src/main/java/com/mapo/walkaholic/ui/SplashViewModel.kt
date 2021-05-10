package com.mapo.walkaholic.ui

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class SplashViewModel(
        private val splashRepository: SplashRepository
) : BaseViewModel(splashRepository) {
    private val _filenameLogoSplash = MutableLiveData("logo_splash.png")
    val filenameLogoSplash: LiveData<String>
        get() = _filenameLogoSplash
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    override fun init() {

    }

    fun getFilenameSplashLogo() = viewModelScope.launch {
        _filenameLogoSplash.value = "logo_splash.png"
    }
}