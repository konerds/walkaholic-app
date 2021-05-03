package com.mapo.walkaholic.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.APISApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.RemoteDataSource
import com.mapo.walkaholic.data.network.SGISApi
import com.mapo.walkaholic.data.repository.GuideRepository
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.databinding.ActivityGuideBinding
import com.mapo.walkaholic.databinding.ActivitySplashscreenBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.fragment_guide.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.M)
class GuideActivity : BaseActivity<GuideViewModel, ActivityGuideBinding, GuideRepository>() {
    val guideList = arrayOf(R.drawable.tutorial1, R.drawable.tutorial2, R.drawable.tutorial3)
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter: PagerAdapter = object : PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val inflater = LayoutInflater.from(container.context)
                val view = inflater.inflate(R.layout.fragment_guide, container, false)

                view.guideIvItem.setImageResource(guideList[position])

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
        guideChipSkip.setOnClickListener {
            //@TODO ACTIVITY ALIVE CHECK
            startActivity(Intent(this, AuthActivity::class.java))
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
        val accessToken = runBlocking { userPreferences.accessToken.first() }
        val api = remoteDataSource.buildRetrofitApi(InnerApi::class.java, accessToken)
        return GuideRepository.getInstance(api, userPreferences)
    }
}