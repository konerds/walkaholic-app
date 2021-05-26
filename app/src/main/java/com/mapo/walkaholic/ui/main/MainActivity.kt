package com.mapo.walkaholic.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.*
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.ActivityMainBinding
import com.mapo.walkaholic.databinding.NaviHamburgerHeaderBinding
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding, MainRepository>(),
    LifecycleOwner {
    private lateinit var bindingNavigationHeader: NaviHamburgerHeaderBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.mActivityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        binding.lifecycleOwner = this
        bindingNavigationHeader = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.navi_hamburger_header,
            binding.mainNvHamburger,
            true
        )
        bindingNavigationHeader.viewModel = viewModel
        setSupportActionBar(binding.mainToolbar)
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.mainDrawerLayout,
            binding.mainToolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        drawerToggle.isDrawerSlideAnimationEnabled = false
        drawerToggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.mainDrawerLayout.addDrawerListener(drawerToggle)
        //drawerToggle.syncState()
        binding.mainToolbar.setNavigationOnClickListener {
            if (binding.mainDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                binding.mainDrawerLayout.closeDrawer(Gravity.LEFT)
            } else {
                binding.mainDrawerLayout.openDrawer(Gravity.LEFT)
            }
        }
        initNavigation()
        binding.mainNvHamburger.setNavigationItemSelectedListener {
            when (it.itemId) {
                /*R.id.actionHbgProfile -> {
                    val navDirection: NavDirections? =
                        MainActivityDirections.actionGlobalActionBnvDashProfile()
                    if (navDirection != null) {
                        findNavController(binding.root.id).navigate(navDirection)
                    }
                    return@setNavigationItemSelectedListener true
                }
                R.id.actionHbgDashCharacterProfile -> {
                    val navDirection: NavDirections? =
                        MainActivityDirections.actionGlobalActionBnvDashCharacterInfo()
                    if (navDirection != null) {
                        findNavController(binding.root.id).navigate(navDirection)
                    }
                    return@setNavigationItemSelectedListener true
                }
                R.id.actionHbgDashCharacterShop -> {
                    val navDirection: NavDirections? =
                        MainActivityDirections.actionGlobalActionBnvDashCharacterShop()
                    if (navDirection != null) {
                        findNavController(binding.root.id).navigate(navDirection)
                    }
                    return@setNavigationItemSelectedListener true
                }
                R.id.actionHbgWalkRecord -> {
                    val navDirection: NavDirections? =
                        MainActivityDirections.actionGlobalActionBnvDashWalkRecord()
                    if (navDirection != null) {
                        findNavController(binding.root.id).navigate(navDirection)
                    }
                    return@setNavigationItemSelectedListener true
                }
                R.id.actionHbgFavorite -> {
                    val navDirection: NavDirections? =
                        MainActivityDirections.actionGlobalFavoritePathFragment()
                    if (navDirection != null) {
                        findNavController(binding.root.id).navigate(navDirection)
                    }
                    return@setNavigationItemSelectedListener true
                }*/
                R.id.actionHbgTutorial -> {
                    startNewActivity(GuideActivity::class.java)
                    return@setNavigationItemSelectedListener true
                }
                R.id.actionHbgLogout -> {
                    //logout()
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
        }
        viewModel.getUser()
        viewModel.userResponse.observe(this, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            bindingNavigationHeader.user = _userResponse.value.data.first()
                            /*lifecycleScope.launch {
                                viewModel.saveJwtToken(it.value.jwtToken)
                            }*/
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _userResponse.value.message,
                                {
                                    viewModel.getUser()
                                },
                                "재시도"
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_userResponse) { viewModel.getUser() }
                }
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    fun logout() = lifecycleScope.launch {
        viewModel.logout()
        userPreferences.removeJwtToken()
        startNewActivity(AuthActivity::class.java)
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

    override fun onDestroy() {
        GlobalApplication.mActivityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun getActivityRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiAPIS = remoteDataSource.buildRetrofitInnerApi(ApisApi::class.java, jwtToken)
        val apiSGIS = remoteDataSource.buildRetrofitInnerApi(SgisApi::class.java, jwtToken)
        return MainRepository(api, apiAPIS, apiSGIS, userPreferences)
    }
}