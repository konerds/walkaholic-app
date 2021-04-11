package com.mapo.walkaholic.ui.service

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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
        when (item.itemId) {
            R.id.action_main -> {
                replaceFragment(DashboardFragment(), "dashboard")
            }
            R.id.action_theme -> {
                replaceFragment(ThemeFragment(), "theme")
            }
            R.id.action_challenge -> {
                replaceFragment(ChallengeFragment(), "challenge")
            }
            R.id.action_map -> {
                replaceFragment(MapFragment(), "map")
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        replaceFragment(DashboardFragment(), "dashboard")
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        when (tag) {
            "dashboard" -> binding.mainToolbar.visibility = View.VISIBLE
            "theme" -> binding.mainToolbar.visibility = View.VISIBLE
            "challenge" -> binding.mainToolbar.visibility = View.VISIBLE
            "map" -> binding.mainToolbar.visibility = View.GONE
        }
        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        fm.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.replace(R.id.main_content, fragment, tag)
        transaction.addToBackStack(tag)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        transaction.isAddToBackStackAllowed
    }

    private fun updateBottomNavigation(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("dashboard")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("theme")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("challenge")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("map")

        if(tag1 != null && tag1.isVisible) {navigation.menu.findItem(R.id.action_main).isChecked = true }
        if(tag2 != null && tag2.isVisible) {navigation.menu.findItem(R.id.action_theme).isChecked = true }
        if(tag3 != null && tag3.isVisible) {navigation.menu.findItem(R.id.action_challenge).isChecked = true }
        if(tag4 != null && tag4.isVisible) {navigation.menu.findItem(R.id.action_map).isChecked = true }
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        updateBottomNavigation(bottomNavigationView)
    }
}