package com.mapo.walkaholic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.FilenameSplashImageResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(
    private val splashRepository: SplashRepository
) : BaseViewModel(splashRepository) {
    private val _filenameSplashImageResponse : MutableLiveData<Resource<FilenameSplashImageResponse>> = MutableLiveData()
    val filenameSplashImageResponse: LiveData<Resource<FilenameSplashImageResponse>>
        get() = _filenameSplashImageResponse

    override fun init() {

    }

    fun getFilenameSplashImage() = viewModelScope.launch {
        _filenameSplashImageResponse.value = splashRepository.getSplashFilename()
    }
}