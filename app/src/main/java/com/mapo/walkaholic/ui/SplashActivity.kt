package com.mapo.walkaholic.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.databinding.ActivitySplashscreenBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.service.MainActivity
import kotlinx.android.synthetic.main.activity_splashscreen.*

@RequiresApi(Build.VERSION_CODES.M)
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        splashIv.alpha = 0f
        splashIv.animate().setDuration(1500).alpha(1f).withEndAction {
            val userPreferences = UserPreferences(this)
            // @TODO First APP Launch Check ... below logic must be inside that
            userPreferences.authToken.asLiveData().observe(this, Observer {
                if (it == null) {
                    startNewActivity(AuthActivity::class.java)
                } else {
                    startNewActivity(MainActivity::class.java)
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            })
        }
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }
}