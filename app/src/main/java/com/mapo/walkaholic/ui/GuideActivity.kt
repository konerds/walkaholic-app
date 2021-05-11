package com.mapo.walkaholic.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.databinding.ActivityGuideBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.fragment_guide.view.*
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.M)
class GuideActivity : BaseActivity<GuideViewModel, ActivityGuideBinding, GuideRepository>() {
    private lateinit var guideList : ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        viewModel.onClickEvent.observe(
            this,
            EventObserver(this@GuideActivity::onClickEvent)
        )
        viewModel.filenameListGuide.observe(this, Observer {
            guideList = it
            val adapter: PagerAdapter = object : PagerAdapter() {
                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val inflater = LayoutInflater.from(container.context)
                    val view = inflater.inflate(R.layout.fragment_guide, container, false)
                    setImageUrl(view.guideIvItem, guideList[position])
                    container.addView(view)
                    return view
                }

                override fun destroyItem(container: ViewGroup, position: Int, guide_pa_obj: Any) {
                    container.removeView(guide_pa_obj as View?)
                }

                override fun isViewFromObject(view: View, guide_pa_obj: Any): Boolean {
                    return view == guide_pa_obj
                }

                override fun getCount(): Int {
                    return guideList.size
                }
            }
            guideVp.adapter = adapter
        })
        viewModel.getFilenameListGuide()
    }

    private fun onClickEvent(name: String) {
        when (name) {
            "tutorialskip" -> {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
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