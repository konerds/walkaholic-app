package com.mapo.walkaholic.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.WalkRecordExistInMonth
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
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else {
                    _userResponse.value = tokenInfo?.id?.let { mainRepository.getUser(it) }
                }
                progressBarVisibility.set(false)
            }
        }
    }

    fun getCalendarDate(userId: Long, walkDate: String) {
        viewModelScope.launch {
            _calendarDateResponse.value = mainRepository.getCalendarDate(userId, walkDate)
        }
    }

    fun getCalendarMonth(userId: Long, walkMonth: String) {
        viewModelScope.launch {
            _calendarMonthResponse.value = mainRepository.getCalendarMonth(userId, walkMonth)
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()
}