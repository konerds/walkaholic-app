package com.mapo.walkaholic.ui.auth

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isNotEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.ActivityAuthBinding
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.android.synthetic.main.fragment_register.view.*

@RequiresApi(Build.VERSION_CODES.M)
class AuthActivity : BaseActivity<AuthViewModel, ActivityAuthBinding, AuthRepository>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.mActivityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val factory = ViewModelFactory(getActivityRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        /*
        userPreferences.jwtToken.asLiveData().observe(this, Observer {
            if (it == null) { } else {
                startNewActivity(MainActivity::class.java as Class<Activity>)
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        })
         */
        /*Log.i(
            ContentValues.TAG, "해쉬값+${getHashKey()}"
        )*/
    }

    override fun onBackPressed() {
        val layoutRegisterAgree = findViewById<LinearLayout>(R.id.registerLayout1)
        val layoutRegisterInput = findViewById<LinearLayout>(R.id.registerLayout2)
        if ((layoutRegisterAgree?.isNotEmpty() == true) && (layoutRegisterInput?.isNotEmpty() == true)) {
            if ((layoutRegisterAgree.visibility == View.GONE) && (layoutRegisterInput.visibility == View.VISIBLE)) {
                val root = findViewById<ConstraintLayout>(R.id.root_regLayout)
                if (root.isNotEmpty()) {
                    root.registerChipAgreeService.isClickable = true
                    root.registerChipAgreePrivacy.isClickable = true
                    root.registerChipAgreeAll.isClickable = true
                    root.registerChipAgreeService.isChecked = false
                    root.registerChipAgreePrivacy.isChecked = false
                    root.registerChipAgreeAll.isChecked = false
                    var targetConstraintSet = ConstraintSet()
                    targetConstraintSet.clone(this, R.layout.fragment_register)
                    targetConstraintSet.setVisibility(R.id.registerLayout1, View.VISIBLE)
                    targetConstraintSet.setVisibility(R.id.registerLayout2, View.GONE)
                    targetConstraintSet.applyTo(root)
                    val transitionConSet = ChangeBounds()
                    transitionConSet.interpolator = AccelerateInterpolator()
                    TransitionManager.beginDelayedTransition(root, transitionConSet)
                }
            } else {
                startNewActivity(AuthActivity::class.java)
            }
            mBackWait = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        GlobalApplication.mActivityList.remove(this)
        super.onDestroy()
    }

    override fun getViewModel(): Class<AuthViewModel> = AuthViewModel::class.java

    override fun getActivityRepository(): AuthRepository {
        val api = remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java)
        return AuthRepository(api, userPreferences)
    }

    /*private fun getHashKey(): String? {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                val packageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                val signatures = packageInfo.signingInfo.apkContentsSigners
                val md = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    return String(Base64.encode(md.digest(), NO_WRAP))
                }
            } else {
                val packageInfo =
                    packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                        ?: return null

                for (signature in packageInfo!!.signatures) {
                    try {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                    } catch (e: NoSuchAlgorithmException) {
                        // ERROR LOG
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return null
    }*/
}