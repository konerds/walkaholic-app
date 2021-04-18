package com.mapo.walkaholic.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.UserCharacterResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository
) : BaseViewModel() {
    override fun init() {}
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _userCharacterResponse: MutableLiveData<Resource<UserCharacterResponse>> =
        MutableLiveData()
    val userCharacterResponse: LiveData<Resource<UserCharacterResponse>>
        get() = _userCharacterResponse

    fun getDash() {
        progressBarVisibility.set(true)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else {
                    _userResponse.value = tokenInfo?.id?.let { repository.getUser(it) }
                    _userCharacterResponse.value = tokenInfo?.id?.let { repository.getUserCharacter(it) }
                }
                progressBarVisibility.set(false)
            }
        }
    }

    fun getUserCharacterName(type: Int) =
        when (type) {
            0 -> "인간"
            1 -> "해골"
            2 -> "오크"
            else -> "[오류]"
        }
}