package com.mapo.walkaholic.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.DashResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DashboardViewModel(
        private val repository: DashboardRepository
) : BaseViewModel() {
    override fun init() {}
    private val _dashResponse: MutableLiveData<Resource<DashResponse>> = MutableLiveData()
    val dashResponse: LiveData<Resource<DashResponse>>
        get() = _dashResponse

    fun getDash() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else _dashResponse.value = tokenInfo?.id?.let { repository.getDash(it) }
            }
        }
    }
}