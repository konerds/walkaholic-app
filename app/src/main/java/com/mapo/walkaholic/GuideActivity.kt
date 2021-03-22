package com.mapo.walkaholic

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.activity_guide.view.*

@RequiresApi(Build.VERSION_CODES.M)
class GuideActivity : AppCompatActivity() {
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val adapter: PagerAdapter = object:PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val inflater = LayoutInflater.from(container.context)
                val view = inflater.inflate(R.layout.fragment_guide, container, false)

                view.ivSplashItem.setImageResource()

                return super.instantiateItem(container, position)
            }
            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                super.destroyItem(container, position, `object`)
            }
            override fun isViewFromObject(view: View, `object`: Any): Boolean {

            }
            override fun getCount(): Int {

            }
        }
    }
}