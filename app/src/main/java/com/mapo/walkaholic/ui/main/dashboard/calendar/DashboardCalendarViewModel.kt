package com.mapo.walkaholic.ui.main.dashboard.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DashboardCalendarViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {}
    private val _calendarDateResponse: MutableLiveData<Resource<WalkRecordResponse>> = MutableLiveData()
    val calendarResponse: LiveData<Resource<WalkRecordResponse>>
        get() = _calendarDateResponse
    private val _calendarMonthResponse: MutableLiveData<Resource<WalkRecordExistInMonthResponse>> = MutableLiveData()
    val calendarMonthResponse: LiveData<Resource<WalkRecordExistInMonthResponse>>
        get() = _calendarMonthResponse
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getWalkRecord(userId: Long, walkDate: String) {
        viewModelScope.launch {
            _calendarDateResponse.value = mainRepository.getWalkRecord(userId, walkDate)
        }
    }

    fun getCalendarMonth(userId: Long, walkMonth: String) {
        viewModelScope.launch {
            _calendarMonthResponse.value = mainRepository.getCalendarMonth(userId, walkMonth)
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()
}