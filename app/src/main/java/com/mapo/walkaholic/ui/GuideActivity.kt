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
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        binding.viewModel = viewModel
        viewModel.onClickEvent.observe(
            this,
            EventObserver(this@GuideActivity::onClickEvent)
        )
        viewModel.filenameListGuide.observe(this, Observer {
            when(it) {
                is Resource.Success -> {
                   if(!it.value.error) {
                       val guideList = it.value.guideInformation
                       val adapter = object : RecyclerView.Adapter<ViewHolder>() {
                           inner class GuideViewHolder(itemView: View) : ViewHolder(itemView) {

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
                               setImageUrl(holder.itemView.guideIvItem, guideList[position].tutorial_filename)
                           }

                           override fun getItemCount(): Int = guideList.size
                       }
                       binding.guideVp.adapter = adapter
                   }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
            }
        })
        viewModel.getFilenameListGuide()
    }

    private fun onClickEvent(name: String) {
        Log.e(TAG, name)
        when (name) {
            "tutorial_skip" -> {
                startNewActivity(AuthActivity::class.java)
            }
            else -> { }
        }
    }

    override fun onBackPressed() {
        if (guideVp.currentItem == 0) {
            super.onBackPressed()
        } else {
            guideVp.currentItem = guideVp.currentItem - 1
        }
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<GuideViewModel> = GuideViewModel::class.java

    override fun getActivityRepository(): GuideRepository {
        val api = remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java)
        return GuideRepository.getInstance(api, userPreferences)
    }
}