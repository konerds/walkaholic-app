package com.mapo.walkaholic.ui.global

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.mapo.walkaholic.R
import java.lang.IllegalArgumentException

class GlobalApplication : Application() {
    lateinit var context: Context

    init {
        instance = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: GlobalApplication? = null
        fun getGlobalApplicationContext(): Context {
            if (instance == null) {
                throw IllegalArgumentException("Not inherited from GlobalApplication!")
            } else {
                return instance!!.applicationContext
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    /* @TODO Kakao Authentication Adapter
    class KakaoAuthAdapter : KakaoAdapter() {
    }
     */

    /* @TODO Process
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable()) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
     */
}