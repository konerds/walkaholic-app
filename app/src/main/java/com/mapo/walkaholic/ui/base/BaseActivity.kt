package com.mapo.walkaholic.ui.base

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.RemoteDataSource
import com.mapo.walkaholic.data.repository.BaseRepository
import com.mapo.walkaholic.ui.main.dashboard.DashboardFragmentDirections
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoFragment
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoFragmentDirections
import com.mapo.walkaholic.ui.main.dashboard.character.shop.DashboardCharacterShopFragment
import com.mapo.walkaholic.ui.main.dashboard.character.shop.DashboardCharacterShopFragmentDirections
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class BaseActivity<VM: BaseViewModel, B: ViewBinding, R:BaseRepository>: AppCompatActivity() {
    protected lateinit var userPreferences: UserPreferences
    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected val remoteDataSource = RemoteDataSource()
    companion object {
        var mBackWait: Long = 0
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        viewModel.showToastEvent.observe(
            this,
            EventObserver(this@BaseActivity::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            this,
            EventObserver(this@BaseActivity::showSnackbarEvent)
        )
    }

    private fun showToastEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                Toast.makeText(
                    this,
                    contents,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSnackbarEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                 /*binding.root.snackbar(contents)*/
            }
        }
    }

    /*override fun onBackPressed() {
        var currentFragment : Fragment? = null
        for (elementFragment in supportFragmentManager.fragments) {
            if (elementFragment.isVisible) {
                currentFragment = elementFragment
            }
        }
        val navDirection: NavDirections? =
        when(currentFragment) {
            is DashboardCharacterShopFragment -> {
                Log.e(TAG, (currentFragment is DashboardCharacterShopFragment).toString())
                DashboardCharacterShopFragmentDirections.actionActionBnvDashCharacterShopToActionBnvDashCharacterInfo()
            }
            is DashboardCharacterInfoFragment -> {
                Log.e(TAG, (currentFragment is DashboardCharacterInfoFragment).toString())
                DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoToActionBnvDash()
            }
            else -> {
                null
            }
        }
    }*/

    /*override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            if ((supportFragmentManager.backStackEntryCount == 0) || ((supportFragmentManager.backStackEntryCount == 0) && (GlobalApplication.activityList.size == 1))) {
                Toast.makeText(this, getString(R.string.err_deny_prev), Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        } else {
            if (System.currentTimeMillis() - mBackWait >= 1000) {
                Toast.makeText(this, getString(R.string.err_multiple_touch), Toast.LENGTH_SHORT)
                        .show()
            }
        }
        mBackWait = System.currentTimeMillis()
        super.onBackPressed()
    }*/

    abstract fun getViewModel(): Class<VM>
    abstract fun getActivityRepository(): R
}