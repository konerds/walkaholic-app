package com.mapo.walkaholic.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class DashboardCharacterInfoDetailViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    /*
        @TODO INVENTORY DATA RESPONSE
     */
    override fun init() {}
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _expTableResponse: MutableLiveData<Resource<ExpTableResponse>> = MutableLiveData()
    val expTableResponse: LiveData<Resource<ExpTableResponse>>
        get() = _expTableResponse
    private val _characterUriList: MutableLiveData<Resource<CharacterUriResponse>> = MutableLiveData()
    val characterUriList: LiveData<Resource<CharacterUriResponse>>
        get() = _characterUriList

    fun getDash() {
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

    fun toAnyToString(any: Any) = any.toString().trim()

    fun getExpTable(exp: Long) {
        viewModelScope.launch {
            _expTableResponse.value = mainRepository.getExpTable(exp)
        }
    }

    fun getUserCharacterName(type: Int) =
            when (type) {
                0 -> "비타씨"
                1 -> "파랑 비타씨"
                2 -> "노랑 비타씨"
                else -> "[오류]"
            }

    fun getCharacterUriList(characterType: String) {
        viewModelScope.launch {
            _characterUriList.value = mainRepository.getCharacterUriList(characterType)
        }
    }
}