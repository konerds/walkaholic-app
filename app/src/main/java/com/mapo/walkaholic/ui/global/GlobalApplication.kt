package com.mapo.walkaholic.ui.global

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.naver.maps.map.NaverMapSdk
import java.lang.IllegalArgumentException

class GlobalApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: GlobalApplication? = null
        val activityList : ArrayList<Activity> = ArrayList<Activity>()
        fun getGlobalApplicationContext(): Context {
            checkNotNull(instance) { "Not inherited from GlobalApplication!" }
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        NaverMapSdk.getInstance(GlobalApplication.getGlobalApplicationContext()).client = NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}