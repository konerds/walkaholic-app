package com.mapo.walkaholic.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.model.GuideInformation
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.databinding.ActivityGuideBinding
import com.mapo.walkaholic.databinding.ItemGuideBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.item_guide.view.*

@RequiresApi(Build.VERSION_CODES.M)
class GuideActivity : BaseActivity<GuideViewModel, ActivityGuideBinding, GuideRepository>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // For Manage Activity Lifecycle
        GlobalApplication.activityList.add(this)
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
            Observe Resource of Tutorial Filename
         */
        viewModel.filenameListGuide.observe(this, Observer { responseGuide ->
            when (responseGuide) {
                is Resource.Success -> {
                    if (!responseGuide.value.error) {
                        binding.guideVp.adapter = GuideAdapter(responseGuide.value.guideInformation)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(responseGuide)
                }
            }
        })
        /*
            Call Rest Function in ViewModel
         */
        viewModel.getFilenameListGuide()
        /*
            Click Listener For Skip Button
         */
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
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<GuideViewModel> = GuideViewModel::class.java

    override fun getActivityRepository(): GuideRepository {
        val api = remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java)
        return GuideRepository(api, userPreferences)
    }
}