package com.mapo.walkaholic.ui.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapo.walkaholic.ui.global.GlobalApplication

abstract class BaseActivity : AppCompatActivity() {
    var mBackWait: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            if (GlobalApplication.activityList.size == 1) {
                Toast.makeText(this, "뒤로 갈 수 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        }
        mBackWait = System.currentTimeMillis()
    }
}