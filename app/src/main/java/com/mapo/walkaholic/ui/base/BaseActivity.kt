package com.mapo.walkaholic.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.RemoteDataSource
import com.mapo.walkaholic.data.repository.BaseRepository
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        userPreferences = UserPreferences(this)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        lifecycleScope.launch { userPreferences.accessToken.first() }
    }

    override fun onBackPressed() {
        /*
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
         */
        super.onBackPressed()
    }

    abstract fun getViewModel(): Class<VM>
    abstract fun getActivityRepository(): R
}