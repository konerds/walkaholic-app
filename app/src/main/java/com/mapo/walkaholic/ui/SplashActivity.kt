package com.mapo.walkaholic.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.databinding.ActivitySplashscreenBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_splashscreen.*
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.M)
class SplashActivity: BaseActivity<SplashViewModel, ActivitySplashscreenBinding, SplashRepository>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        viewModel.filenameLogoSplash.observe(this, Observer {
            setImageUrl(binding.splashIv, it)
        })
        viewModel.getFilenameSplashLogo()
        splashIv.alpha = 0f
        splashIv.animate().setDuration(1500).alpha(1f).withEndAction {
            // @TODO First APP Launch Check ... below logic must be inside that
            userPreferences.isFirst.asLiveData().observe(this, Observer {
                if(it == null) {
                    startNewActivity(GuideActivity::class.java as Class<Activity>)
                } else {
                    startNewActivity(AuthActivity::class.java as Class<Activity>)
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            })
        }
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun getActivityRepository(): SplashRepository {
        val api = remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java)
        return SplashRepository.getInstance(api, userPreferences)
    }
}