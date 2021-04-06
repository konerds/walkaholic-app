package com.mapo.walkaholic.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import com.mapo.walkaholic.R
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.fragment_guide.view.*

@RequiresApi(Build.VERSION_CODES.M)
class GuideActivity : BaseActivity() {
    val guideList = arrayOf(R.drawable.tutorial1, R.drawable.tutorial2, R.drawable.tutorial3)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        when (Build.VERSION.SDK_INT) {
            in (Build.VERSION_CODES.KITKAT..(Build.VERSION_CODES.M) - 1) -> {
                @Suppress("DEPRECATION")
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            in (Build.VERSION_CODES.M)..Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.TRANSPARENT
            }
        }
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

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

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (guideVp.currentItem == 0) {
            super.onBackPressed()
        } else {
            guideVp.currentItem = guideVp.currentItem - 1
        }
    }
}