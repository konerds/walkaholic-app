package com.mapo.walkaholic.ui

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
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
            Observe Click Event
         */
        viewModel.onClickEvent.observe(
            this,
            // Delegate Click Event To onClickEvent Function
            EventObserver(this@GuideActivity::onClickEvent)
        )
        /*
            Observe Resource of Tutorial Filename
         */
        viewModel.filenameListGuide.observe(this, Observer { responseGuide ->
            when (responseGuide) {
                is Resource.Success -> {
                    if (!responseGuide.value.error) {
                        val guideList = responseGuide.value.guideInformation
                        val adapter = object : RecyclerView.Adapter<ViewHolder>() {
                            inner class GuideViewHolder(itemView: View) : ViewHolder(itemView) {
                                private val binding = ItemGuideBinding.inflate(layoutInflater)
                            }

                            override fun onCreateViewHolder(
                                parent: ViewGroup,
                                position: Int
                            ): ViewHolder {
                                val inflater = LayoutInflater.from(parent.context)
                                val view = inflater.inflate(R.layout.item_guide, parent, false)
                                return GuideViewHolder(view)
                            }

                            override fun onBindViewHolder(
                                holder: ViewHolder,
                                position: Int
                            ) {
                                setImageUrl(
                                    holder.itemView.guideIvItem,
                                    guideList[position].tutorial_filename
                                )
                            }

                            override fun getItemCount(): Int = guideList.size
                        }
                        binding.guideVp.adapter = adapter
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
    }

    /*
        Funtion For Handle Click Event
     */
    private fun onClickEvent(name: String) {
        when (name) {
            "tutorial_skip" -> {
                startNewActivity(AuthActivity::class.java)
            }
            else -> {
            }
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
        return GuideRepository.getInstance(api, userPreferences)
    }
}