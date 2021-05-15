package com.mapo.walkaholic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(
    private val splashRepository: SplashRepository
) : BaseViewModel(splashRepository) {
    private val _filenameLogoSplash = MutableLiveData("logo_splash.png")
    val filenameLogoSplash: LiveData<String>
        get() = _filenameLogoSplash

    override fun init() {

    }

    fun getFilenameSplashLogo() = viewModelScope.launch {
        _filenameLogoSplash.value = "logo_splash.png"
    }
}