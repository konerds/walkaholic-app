package com.mapo.walkaholic.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mapo.walkaholic.R

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
        val first: Boolean = pref.getBoolean("isFirst", false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        if (!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()
            Handler().postDelayed({
                startActivity(Intent(this, GuideActivity::class.java))
                finish()
            }, SPLASH_DISPLAY_TIME.toLong())
        } else {
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, SPLASH_DISPLAY_TIME.toLong())
        }
    }
}