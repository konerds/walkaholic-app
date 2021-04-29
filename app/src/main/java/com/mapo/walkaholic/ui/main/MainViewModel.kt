package com.mapo.walkaholic.ui.main

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.Event
import kotlinx.coroutines.launch

class MainViewModel(
        private val dashRepository: MainRepository
) : ViewModel() {
    private val _onClickEvent = MutableLiveData<Event<String>>()
    val onClickEvent: LiveData<Event<String>>
        get() = _onClickEvent

    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    fun onClickEvent(name: String) {
        viewModelScope.launch {
            _onClickEvent.value = Event(name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i(ContentValues.TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
        }
    }

    suspend fun saveAuthToken() {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        logout()
                    } else {
                        logout()
                    }
                } else {
                    tokenInfo!!.id.toString().trim().let {
                        if (it != null) {
                            viewModelScope.launch {
                                dashRepository.saveAuthToken(it)
                            }
                        } else {
                            logout()
                        }
                    }
                }
            }
        } else {
            logout()
        }
    }

    fun getUser() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else {
                    _userResponse.value = tokenInfo?.id?.let { dashRepository.getUser(it) }
                }
            }
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()
}