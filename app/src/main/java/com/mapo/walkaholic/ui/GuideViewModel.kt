package com.mapo.walkaholic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.GuideInformationResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class GuideViewModel(
    private val guideRepository: GuideRepository
) : BaseViewModel(guideRepository) {
    private val _filenameGuideImageResponse: MutableLiveData<Resource<GuideInformationResponse>> =
        MutableLiveData()
    val filenameGuideImageResponse: LiveData<Resource<GuideInformationResponse>>
        get() = _filenameGuideImageResponse

    fun getFilenameGuideImage() = viewModelScope.launch {
        _filenameGuideImageResponse.value = guideRepository.getFilenameGuideImage()
    }

    override fun init() {

    }
}