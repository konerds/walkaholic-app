package com.mapo.walkaholic.ui.base

import android.content.ContentValues
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

    private val _showToastEvent = MutableLiveData<Event<String>>()
    val showToastEvent : LiveData<Event<String>>
        get() = _showToastEvent
    private val _showSnackbarEvent = MutableLiveData<Event<String>>()
    val showSnackbarEvent : LiveData<Event<String>>
        get() = _showSnackbarEvent

    fun showToastEvent(contents: String) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _showToastEvent.value = Event(contents)
            progressBarVisibility.set(false)
        }
    }

    fun showSnackbarEvent(contents: String) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _showSnackbarEvent.value = Event(contents)
            progressBarVisibility.set(false)
        }
    }

    fun toLongToInt(valueLong: Long) = valueLong.toFloat().toInt()

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

    fun getPixelsPerMetre() = repository.getPixelsPerMetre()

    fun getResourceBaseUri() = repository.getResourceBaseUri()

    suspend fun saveIsFirst() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            repository.saveIsFirst(true)
        }
        progressBarVisibility.set(false)
    }

    suspend fun saveJwtToken(jwtToken: String) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            repository.saveAuthToken(jwtToken)
        }
        progressBarVisibility.set(false)
    }

    suspend fun saveIsLocationGranted() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            repository.saveIsLocationGranted(true)
        }
        progressBarVisibility.set(false)
    }

    abstract fun init()
}