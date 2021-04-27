package com.mapo.walkaholic.ui.main

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.ActivityMainBinding
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(mainToolbar)
        binding.lifecycleOwner = this
        initNavigation()
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.mainDrawerLayout,
            binding.mainToolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        binding.mainDrawerLayout.setDrawerListener(drawerToggle)
        drawerToggle.syncState()
        binding.mainToolbar.setNavigationOnClickListener {
            if (binding.mainDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                binding.mainDrawerLayout.closeDrawer(Gravity.LEFT)
            } else {
                binding.mainDrawerLayout.openDrawer(Gravity.LEFT)
            }
        }
    }

    private fun initNavigation() {
        val navController = Navigation.findNavController(this, R.id.mainFragmentHost)
        NavigationUI.setupWithNavController(mainNvHamburger, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, mainDrawerLayout)
        NavigationUI.setupWithNavController(mainBottomNavigation, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.mainFragmentHost),
            mainDrawerLayout
        )
    }

    /*
    override fun getDrawerToggleDelegate(): ActionBarDrawerToggle.Delegate? {
        return super.getDrawerToggleDelegate()
    }
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.actionHbgFavorite -> {
                return true
            }
            R.id.actionHbgDashCharacterInfo -> {
                return true
            }
            R.id.actionHbgDashCharacterShop -> {
                return true
            }
            R.id.actionHbgTutorial -> {
                return true
            }
            R.id.actionHbgLogout -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }
}