package com.mapo.walkaholic.ui.base

import android.content.ContentValues.TAG
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.repository.BaseRepository
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    private val repository: BaseRepository
) : ViewModel() {
    val progressBarVisibility = ObservableBoolean(false)
    private val _onClickEvent = MutableLiveData<Event<String>>()
    val onClickEvent: LiveData<Event<String>>
        get() = _onClickEvent

    fun onClickEvent(name: String) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _onClickEvent.value = Event(name)
            progressBarVisibility.set(false)
        }
    }

    fun logout() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
        }
    }

    suspend fun saveAuthToken() {
        progressBarVisibility.set(true)
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
                                repository.saveAuthToken(it)
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
        progressBarVisibility.set(false)
    }

    abstract fun init()
}