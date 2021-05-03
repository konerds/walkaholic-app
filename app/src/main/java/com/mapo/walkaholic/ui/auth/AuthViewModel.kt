package com.mapo.walkaholic.ui.auth

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.StringResponse
import com.mapo.walkaholic.data.model.response.TermResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}

    fun getAuth(callback: (OAuthToken?, Throwable?) -> Unit) = viewModelScope.launch {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(GlobalApplication.getGlobalApplicationContext())) {
            UserApiClient.instance.loginWithKakaoTalk(
                GlobalApplication.getGlobalApplicationContext(),
                callback = callback
            )
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                GlobalApplication.getGlobalApplicationContext(),
                callback = callback
            )
        }
    }
}