package com.mapo.walkaholic.ui

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.databinding.ActivitySplashscreenBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_splashscreen.*

class SplashActivity :
    BaseActivity<SplashViewModel, ActivitySplashscreenBinding, SplashRepository>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // For Manage Activity Lifecycle
        GlobalApplication.mActivityList.add(this)
        // Call Parent Function
        super.onCreate(savedInstanceState)
        /*
            View Binding
         */
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Shared Preference
        userPreferences = UserPreferences(this)
        /*
            Set ViewModel
         */
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        /*
            For 2-Way Binding With XML
         */
        binding.viewModel = viewModel
        /*
            Call Rest Function in ViewModel
         */
        viewModel.getFilenameSplashImage()
        /*
            Observe Resource of Splash Logo Filename
         */
        viewModel.filenameSplashImageResponse.observe(this, Observer { _responseSplash ->
            when (_responseSplash) {
                is Resource.Success -> {
                    when (_responseSplash.value.code) {
                        "200" -> {
                            binding.filenameSplashImage = _responseSplash.value.data.first()
                            /*
                                Splash Logo Animation
                            */
                            splashIv.alpha = 0f
                            splashIv.animate().setDuration(1500).alpha(1f).withEndAction {
                                /*
                                    Check Is Launched First
                                 */
                                userPreferences.isFirst.asLiveData()
                                    .observe(this, Observer { isFirst ->
                                        if (isFirst == null) {
                                            // If First, Move Activity To Tutorial Activity
                                            startNewActivity(GuideActivity::class.java as Class<Activity>)
                                        } else {
                                            // If Not First, Move Activity To Auth Activity
                                            startNewActivity(AuthActivity::class.java as Class<Activity>)
                                        }
                                        overridePendingTransition(
                                            android.R.anim.fade_in,
                                            android.R.anim.fade_out
                                        )
                                    })
                            }
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _responseSplash.value.message,
                                {
                                    viewModel.getFilenameSplashImage()
                                },
                                "재시도"
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_responseSplash) { viewModel.getFilenameSplashImage() }
                }
            }
        })
    }

    override fun onDestroy() {
        // For Manage Activity Lifecycle
        GlobalApplication.mActivityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun getActivityRepository(): SplashRepository {
        val api = remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java)
        return SplashRepository(api, userPreferences)
    }
}