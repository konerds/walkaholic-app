package com.mapo.walkaholic.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.mapo.walkaholic.R
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication

@RequiresApi(Build.VERSION_CODES.M)
class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }
}