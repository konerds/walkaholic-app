package com.mapo.walkaholic.ui.service

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.response.CharacterResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.launch

class DashboardViewModel(
        private val repository: DashboardRepository
) : BaseViewModel() {
    override fun init() {}
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    fun getUser() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else _userResponse.value = tokenInfo?.id?.let { repository.getUser(it) }
            }
        }
    }

    private val _characterResponse: MutableLiveData<Resource<CharacterResponse>> = MutableLiveData()
    val characterResponse: LiveData<Resource<CharacterResponse>>
        get() = _characterResponse

    fun getCharacter() = viewModelScope.launch {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else _characterResponse.value = tokenInfo?.id?.let { repository.getCharacter(it) }
            }
        }
    }
}