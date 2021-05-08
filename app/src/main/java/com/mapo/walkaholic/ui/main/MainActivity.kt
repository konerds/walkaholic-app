package com.mapo.walkaholic.ui.main

import android.content.ContentValues
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
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
import com.mapo.walkaholic.data.repository.SplashRepository
import com.mapo.walkaholic.databinding.ActivityMainBinding
import com.mapo.walkaholic.databinding.NaviHamburgerHeaderBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding, MainRepository>(), LifecycleOwner {
    private lateinit var bindingNavigationHeader : NaviHamburgerHeaderBinding
    private lateinit var drawerToggle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val mainFactory = ViewModelFactory(getMainRepository())
        viewModel = ViewModelProvider(this, mainFactory).get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        bindingNavigationHeader = DataBindingUtil.inflate(layoutInflater, R.layout.navi_hamburger_header, binding.mainNvHamburger, true)
        bindingNavigationHeader.viewModel = viewModel
        setSupportActionBar(binding.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        drawerToggle = ActionBarDrawerToggle(
                this,
                binding.mainDrawerLayout,
                binding.mainToolbar,
                R.string.openDrawer,
                R.string.closeDrawer
        )
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        binding.mainDrawerLayout.addDrawerListener(drawerToggle)
        initNavigation()
        binding.mainToolbar.setNavigationOnClickListener {
            if (binding.mainDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                binding.mainDrawerLayout.closeDrawer(Gravity.LEFT)
            } else {
                binding.mainDrawerLayout.openDrawer(Gravity.LEFT)
            }
        }
        viewModel.userResponse.observe(this, Observer {
            Log.i(
                    ContentValues.TAG, "Observing... ${it}"
            )
            when(it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        bindingNavigationHeader.user = it.value.user
                        Log.i(
                                ContentValues.TAG, "${it.value.user}"
                        )
                    } else {
                        Toast.makeText(
                                this,
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                        ).show()
                        logout()
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
            }
        })
        viewModel.getUser()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                logout()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    fun logout() = lifecycleScope.launch {
        viewModel.logout()
        userPreferences.removeAuthToken()
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

    fun getMainRepository(): MainRepository {
        val accessToken = runBlocking { userPreferences.accessToken.first() }
        val api = remoteDataSource.buildRetrofitApi(InnerApi::class.java, accessToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(APISApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SGISApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun getActivityRepository(): MainRepository {
        val accessToken = runBlocking { userPreferences.accessToken.first() }
        val api = remoteDataSource.buildRetrofitApi(InnerApi::class.java, accessToken)
        val apiAPIS = remoteDataSource.buildRetrofitApi(APISApi::class.java, accessToken)
        val apiSGIS = remoteDataSource.buildRetrofitApi(SGISApi::class.java, accessToken)
        return MainRepository.getInstance(api, apiAPIS, apiSGIS, userPreferences)
    }
}