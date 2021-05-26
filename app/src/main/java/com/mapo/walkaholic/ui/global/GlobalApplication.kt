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
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        @SuppressLint("StaticFieldLeak")
        private var instance: GlobalApplication? = null
        fun getGlobalApplicationContext(): Context {
            checkNotNull(instance) { "Not inherited from GlobalApplication!" }
            return instance!!.applicationContext
        }
        val mActivityList: ArrayList<Activity> = ArrayList()

        // Initiation To Mapo
        var currentLng: String = "126.901609"
        var currentLat: String = "37.566168"
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