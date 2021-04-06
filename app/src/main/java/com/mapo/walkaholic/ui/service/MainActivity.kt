package com.mapo.walkaholic.ui.service

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
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
        val transaction: FragmentTransaction = fm.beginTransaction()
        when (item.itemId) {
            R.id.action_main -> {
                binding.mainToolbar.visibility = View.VISIBLE
                binding.mainBtnPrev.visibility = View.VISIBLE
                fm.popBackStackImmediate("dashboard", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var dashboardFragment = DashboardFragment()
                transaction.replace(R.id.main_content, dashboardFragment, "dashboard").addToBackStack("dashboard")
            }
            R.id.action_theme -> {
                binding.mainToolbar.visibility = View.VISIBLE
                binding.mainBtnPrev.visibility = View.VISIBLE
                fm.popBackStackImmediate("theme", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var themeFragment = ThemeFragment()
                transaction.replace(R.id.main_content, themeFragment, "theme").addToBackStack("theme")
            }
            R.id.action_challenge -> {
                binding.mainToolbar.visibility = View.VISIBLE
                binding.mainBtnPrev.visibility = View.VISIBLE
                fm.popBackStackImmediate("challenge", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var challengeFragment = ChallengeFragment()
                transaction.replace(R.id.main_content, challengeFragment, "challenge").addToBackStack("challenge")
            }
            R.id.action_map -> {
                binding.mainToolbar.visibility = View.GONE
                fm.popBackStackImmediate("map", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                var mapFragment = MapFragment()
                transaction.replace(R.id.main_content, mapFragment, "map").addToBackStack("map")
            }
        }
        transaction.commit()
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

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        binding.mainToolbar.visibility = View.VISIBLE
        binding.mainBtnPrev.visibility = View.VISIBLE
        fm.popBackStackImmediate("dashboard", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val dashboardFragment = DashboardFragment()
        transaction.replace(R.id.main_content, dashboardFragment, "dashboard").addToBackStack("dashboard")
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        transaction.isAddToBackStackAllowed

        binding.mainBtnPrev.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            if ((supportFragmentManager.backStackEntryCount == 0) && (GlobalApplication.activityList.size == 1)) {
                Toast.makeText(this, "뒤로 갈 수 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }
}