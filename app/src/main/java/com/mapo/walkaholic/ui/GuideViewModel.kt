package com.mapo.walkaholic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class GuideViewModel(
    private val guideRepository: GuideRepository
) : BaseViewModel(guideRepository) {
    private val _filenameListGuide = MutableLiveData(ArrayList<String>())
    val filenameListGuide: LiveData<ArrayList<String>>
        get() = _filenameListGuide
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    fun getFilenameListGuide() = viewModelScope.launch {
        val arrayListGuideFilename = ArrayList<String>()
        arrayListGuideFilename.add("tutorial1.png")
        arrayListGuideFilename.add("tutorial2.png")
        arrayListGuideFilename.add("tutorial3.png")
        _filenameListGuide.value = arrayListGuideFilename
    }

    override fun init() {

    }
}