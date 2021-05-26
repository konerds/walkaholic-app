package com.mapo.walkaholic.ui.global

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.mapo.walkaholic.R
import com.mapo.walkaholic.ui.confirmDialog
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.util.FusedLocationSource

class GlobalApplication : Application(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
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