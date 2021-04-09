package com.mapo.walkaholic.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapo.walkaholic.ui.global.GlobalApplication

abstract class BaseActivity : AppCompatActivity() {
    var mBackWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        /*
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
         */
        /*
        @Suppress("DEPRECATION")
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
         */
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onBackPressed() {
        if(!isMultipleClicked()) {
            if (((supportFragmentManager.backStackEntryCount == 0) || (supportFragmentManager.backStackEntryCount == 1)) && (GlobalApplication.activityList.size == 1)) {
                Toast.makeText(this, "뒤로 갈 수 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        } else {
            Toast.makeText(this, "뒤로 갈 수 없습니다", Toast.LENGTH_SHORT).show()
        }
        setBackWait()
    }

    fun isMultipleClicked(): Boolean {
        return System.currentTimeMillis() - mBackWait < 2000
    }

    fun setBackWait(): Unit {
        mBackWait = System.currentTimeMillis()
    }
}