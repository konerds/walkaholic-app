package com.mapo.walkaholic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.GuideInformationResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class GuideViewModel(
    private val guideRepository: GuideRepository
) : BaseViewModel(guideRepository) {
    private val _filenameListGuide : MutableLiveData<Resource<GuideInformationResponse>> = MutableLiveData()
    val filenameListGuide: LiveData<Resource<GuideInformationResponse>>
        get() = _filenameListGuide
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    fun getFilenameListGuide() = viewModelScope.launch {
        _filenameListGuide.value = guideRepository.getTutorialFilenames()
    }

    override fun init() {

    }
}