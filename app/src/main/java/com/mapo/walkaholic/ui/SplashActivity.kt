package com.mapo.walkaholic.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.service.MainActivity

@RequiresApi(Build.VERSION_CODES.M)
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        val userPreferences = UserPreferences(this)
        // @TODO First APP Launch Check ... below logic must be inside that
        userPreferences.authToken.asLiveData().observe(this, Observer {
            if (it == null) {
                startNewActivity(AuthActivity::class.java)
            } else {
                startNewActivity(MainActivity::class.java)
            }
        })
    }
    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }
}