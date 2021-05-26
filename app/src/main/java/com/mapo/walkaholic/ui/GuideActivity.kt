package com.mapo.walkaholic.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.databinding.ActivityGuideBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : BaseActivity<GuideViewModel, ActivityGuideBinding, GuideRepository>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // For Manage Activity Lifecycle
        GlobalApplication.mActivityList.add(this)
        // Call Parent Function
        super.onCreate(savedInstanceState)
        /*
            View Binding
         */
        binding = ActivityGuideBinding.inflate(layoutInflater)
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
        viewModel.getFilenameGuideImage()
        /*
            Observe Resource of Tutorial Filename
         */
        viewModel.filenameGuideImageResponse.observe(this, Observer { _filenameGuideImageResponse ->
            when (_filenameGuideImageResponse) {
                is Resource.Success -> {
                    when (_filenameGuideImageResponse.value.code) {
                        "200" -> {
                            binding.guideVp.adapter = GuideAdapter(_filenameGuideImageResponse.value.data)
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _filenameGuideImageResponse.value.message,
                                {
                                    viewModel.getFilenameGuideImage()
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
                    handleApiError(_filenameGuideImageResponse) { _filenameGuideImageResponse }
                }
            }
        })
        binding.guideChipSkip.setOnClickListener {
            startNewActivity(AuthActivity::class.java)
        }
    }

    /*
        Handle Back Press Action
     */
    override fun onBackPressed() {
        if (guideVp.currentItem == 0) {
            super.onBackPressed()
        } else {
            guideVp.currentItem = guideVp.currentItem - 1
        }
    }

    override fun onDestroy() {
        // For Manage Activity Lifecycle
        GlobalApplication.mActivityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<GuideViewModel> = GuideViewModel::class.java

    override fun getActivityRepository(): GuideRepository {
        val api = remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java)
        return GuideRepository(api, userPreferences)
    }
}