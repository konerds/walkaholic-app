package com.mapo.walkaholic.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapo.walkaholic.R
import com.mapo.walkaholic.ui.global.GlobalApplication

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        var mBackWait: Long = 0
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onBackPressed() {
        /*
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            if ((supportFragmentManager.backStackEntryCount == 0) || ((supportFragmentManager.backStackEntryCount == 0) && (GlobalApplication.activityList.size == 1))) {
                Toast.makeText(this, getString(R.string.err_deny_prev), Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        } else {
            if (System.currentTimeMillis() - mBackWait >= 1000) {
                Toast.makeText(this, getString(R.string.err_multiple_touch), Toast.LENGTH_SHORT)
                        .show()
            }
        }
        mBackWait = System.currentTimeMillis()
         */
        super.onBackPressed()
    }
}