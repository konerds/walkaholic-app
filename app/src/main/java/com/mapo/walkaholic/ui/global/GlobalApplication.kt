package com.mapo.walkaholic.ui.global

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
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
        setAllActivitySettings()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))
    }

    private fun setAllActivitySettings() {

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}