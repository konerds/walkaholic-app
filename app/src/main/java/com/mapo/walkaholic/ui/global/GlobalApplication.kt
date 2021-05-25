package com.mapo.walkaholic.ui.global

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.mapo.walkaholic.R
import com.naver.maps.map.NaverMapSdk

class GlobalApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: GlobalApplication? = null
        fun getGlobalApplicationContext(): Context {
            checkNotNull(instance) { "Not inherited from GlobalApplication!" }
            return instance!!.applicationContext
        }

        val activityList: ArrayList<Activity> = ArrayList()
        var kakaoTokenInfo: AccessTokenInfo? = null

        var currentLng: String = "37.535938297705925"
        var currentLat: String = "127.00464302761901"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
            } else {
                if (tokenInfo != null) {
                    kakaoTokenInfo = tokenInfo
                }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}