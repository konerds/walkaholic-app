package com.mapo.walkaholic.ui.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapo.walkaholic.ui.global.GlobalApplication

abstract class BaseActivity : AppCompatActivity() {
    var mBackWait: Long = 0

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