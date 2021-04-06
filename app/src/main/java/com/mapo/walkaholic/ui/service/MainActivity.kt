package com.mapo.walkaholic.ui.service

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.ActivityMainBinding
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fm = supportFragmentManager
        val transaction : FragmentTransaction = fm.beginTransaction()
        when (item.itemId) {
            R.id.action_main -> {
                binding.mainToolbar.visibility = View.VISIBLE
                binding.mainBtnPrev.visibility = View.VISIBLE
                fm.popBackStack("dashboard", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var dashboardFragment = DashboardFragment()
                transaction.replace(R.id.main_content, dashboardFragment).addToBackStack("dashboard")
            }
            R.id.action_theme -> {
                binding.mainToolbar.visibility = View.VISIBLE
                binding.mainBtnPrev.visibility = View.VISIBLE
                fm.popBackStack("theme", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var themeFragment = ThemeFragment()
                transaction.replace(R.id.main_content, themeFragment).addToBackStack("theme")
            }
            R.id.action_challenge -> {
                binding.mainToolbar.visibility = View.VISIBLE
                binding.mainBtnPrev.visibility = View.VISIBLE
                fm.popBackStack("challenge", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var challengeFragment = ChallengeFragment()
                transaction.replace(R.id.main_content, challengeFragment).addToBackStack("challenge")
            }
            R.id.action_map -> {
                binding.mainToolbar.visibility = View.GONE
                fm.popBackStack("map", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var mapFragment = MapFragment()
                transaction.replace(R.id.main_content, mapFragment).addToBackStack("map")
            }
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        transaction.isAddToBackStackAllowed
        return true
    }

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.mainToolbar.visibility = View.VISIBLE
        binding.mainBtnPrev.visibility = View.VISIBLE
        var dashboardFragment = DashboardFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, dashboardFragment).addToBackStack(null)
            .commit()

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        binding.mainBtnPrev.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {

        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }
}