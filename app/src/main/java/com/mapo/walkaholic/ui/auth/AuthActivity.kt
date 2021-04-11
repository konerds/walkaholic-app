package com.mapo.walkaholic.ui.auth

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isNotEmpty
import androidx.navigation.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.ActivityAuthBinding
import com.mapo.walkaholic.ui.base.BaseActivity
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.android.synthetic.main.fragment_register.view.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@RequiresApi(Build.VERSION_CODES.M)
class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalApplication.activityList.add(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        /*
        Log.i(
            ContentValues.TAG, "${getHashKey()}"
        )
         */
    }

    override fun onDestroy() {
        GlobalApplication.activityList.remove(this)
        super.onDestroy()
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

    /*
    private fun getHashKey(): String? {
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
    }
     */
}