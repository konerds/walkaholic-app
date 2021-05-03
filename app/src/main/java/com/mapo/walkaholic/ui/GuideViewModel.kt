package com.mapo.walkaholic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.ui.base.BaseViewModel

class GuideViewModel(
        private val guideRepository: GuideRepository
) : BaseViewModel(guideRepository) {
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    fun toAnyToString(any: Any) = any.toString().trim()

    override fun init() {

    }
}