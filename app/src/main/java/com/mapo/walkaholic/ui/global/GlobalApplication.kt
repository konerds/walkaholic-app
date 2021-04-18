package com.mapo.walkaholic.ui.global

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.mapo.walkaholic.R
import com.naver.maps.map.NaverMapSdk

class GlobalApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: GlobalApplication? = null
        val activityList: ArrayList<Activity> = ArrayList<Activity>()
        fun getGlobalApplicationContext(): Context {
            checkNotNull(instance) { "Not inherited from GlobalApplication!" }
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        NaverMapSdk.getInstance(this).client =
                NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}