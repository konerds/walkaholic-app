package com.mapo.walkaholic.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.UserCharacter
import com.mapo.walkaholic.data.model.response.ExpTableResponse
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
    private val _userCharacterResponse: MutableLiveData<Resource<UserCharacterResponse>> = MutableLiveData()
    val userCharacterResponse: LiveData<Resource<UserCharacterResponse>>
        get() = _userCharacterResponse
    private val _expTableResponse: MutableLiveData<Resource<ExpTableResponse>> = MutableLiveData()
    val expTableResponse: LiveData<Resource<ExpTableResponse>>
        get() = _expTableResponse

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

    fun toLongToString(long:Long) = long.toString().trim()

    fun getExpTable(exp:Long) {
        viewModelScope.launch {
            _expTableResponse.value = repository.getExpTable(exp)
        }
    }

    fun getUserCharacterName(type: Int) =
            when (type) {
                0 -> "인간"
                1 -> "오크"
                2 -> "해골"
                else -> "[오류]"
            }
}