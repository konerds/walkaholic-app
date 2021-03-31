package com.mapo.walkaholic.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.main.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_TIME = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        when (Build.VERSION.SDK_INT) {
            in (Build.VERSION_CODES.KITKAT..(Build.VERSION_CODES.M) - 1) -> {
                @Suppress("DEPRECATION")
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            in (Build.VERSION_CODES.M)..Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.TRANSPARENT
            }
        }
        val pref = this.getPreferences(0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        val userPreferences = UserPreferences(this)
        userPreferences.firstToken.asLiveData().observe(this, Observer {
            if (it == true || it == null) {
                lifecycleScope.launch {
                    userPreferences.saveNotFirst(false)
                }
                startNewActivity(GuideActivity::class.java)
            } else {
                userPreferences.authToken.asLiveData().observe(this, Observer {
                    if (it == null) {
                        startNewActivity(AuthActivity::class.java)
                    } else {
                        startNewActivity(MainActivity::class.java)
                    }
                })
            }
        })
    }
}