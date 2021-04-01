package com.mapo.walkaholic.ui.service

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_main -> {
                var dashboardFragment = DashboardFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, dashboardFragment)
                        .commit()
                return true
            }
            R.id.action_theme -> {
                var themeFragment = ThemeFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, themeFragment).commit()
                return true
            }
            R.id.action_challenge -> {
                var challengeFragment = ChallengeFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, challengeFragment).commit()
                return true
            }
            R.id.action_map -> {
                var mapFragment = MapFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, mapFragment)
                        .commit()
                return true
            }
        }
        return false
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
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }
}