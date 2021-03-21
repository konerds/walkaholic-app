package com.mapo.walkaholic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.core.view.isNotEmpty
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.mapo.walkaholic.databinding.ActivityRegcontentsBinding
import com.mapo.walkaholic.databinding.ActivityRegisterBinding
import org.json.JSONObject
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.M)
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var bindingContents: ActivityRegcontentsBinding
    var isRegOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
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
        bindingContents = ActivityRegcontentsBinding.inflate(layoutInflater)
        val view = bindingContents.root
        setContentView(view)
        bindingContents.signupAgreea.setOnClickListener {
            if (bindingContents.signupAgreea.isChecked && bindingContents.signupAgreeb.isChecked) {
                bindingContents.signupAgreeall.isChecked = true
                bindingContents.signupAgreeall.performClick()
            }
        }
        bindingContents.signupAgreeb.setOnClickListener {
            if (bindingContents.signupAgreeb.isChecked && bindingContents.signupAgreea.isChecked) {
                bindingContents.signupAgreeall.isChecked = true
                bindingContents.signupAgreeall.performClick()
            }
        }
        bindingContents.signupAgreeall.setOnClickListener {
            bindingContents.signupAgreea.isClickable = false
            bindingContents.signupAgreeb.isClickable = false
            bindingContents.signupAgreeall.isClickable = false
            bindingContents.signupAgreea.isChecked = true
            bindingContents.signupAgreeb.isChecked = true
            bindingContents.signupAgreeall.isChecked = true
            isRegOpen = !isRegOpen
            Toast.makeText(
                    applicationContext,
                    getString(R.string.signup_agree),
                    Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({
                updateView(when (isRegOpen) {
                    true -> R.layout.activity_register
                    false -> R.layout.activity_regcontents
                })
            }, 250)
        }

        bindingContents.signupSignupButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userEmail: String = bindingContents.signupEmailEdittext.text.toString()
            val userPassword: String = bindingContents.signupPasswordEdittext.text.toString()
            val userConfirmPassword: String =
                    bindingContents.signupConfirmpwEdittext.text.toString()
            val userName: String = bindingContents.signupNameEdittext.text.toString()
            when {
                userEmail.isEmpty() || !((EMAIL_ADDRESS as Pattern).matcher(userEmail)
                        .matches()) -> {
                    bindingContents.signupEmailEdittext.error =
                            "${getString(R.string.email)}을 ${getString(R.string.input_fail_null)}"
                    bindingContents.signupEmailEdittext.isFocusableInTouchMode = true
                    bindingContents.signupEmailEdittext.requestFocus()
                    imm.showSoftInput(bindingContents.signupEmailEdittext, 0)
                    imm.hideSoftInputFromWindow(bindingContents.signupEmailEdittext.windowToken, 0)
                }
                userPassword.isEmpty() -> {
                    bindingContents.signupPasswordEdittext.error =
                            "${getString(R.string.password)}를 ${getString(R.string.input_fail_null)}"
                    bindingContents.signupPasswordEdittext.isFocusableInTouchMode = true
                    bindingContents.signupPasswordEdittext.requestFocus()
                    imm.showSoftInput(bindingContents.signupPasswordEdittext, 0)
                    imm.hideSoftInputFromWindow(
                            bindingContents.signupPasswordEdittext.windowToken,
                            0
                    )
                }
                userConfirmPassword.isEmpty() -> {
                    bindingContents.signupConfirmpwEdittext.error =
                            "${getString(R.string.confirmpw)}을 ${getString(R.string.input_fail_null)}"
                    bindingContents.signupConfirmpwEdittext.isFocusableInTouchMode = true
                    bindingContents.signupConfirmpwEdittext.requestFocus()
                    imm.showSoftInput(bindingContents.signupConfirmpwEdittext, 0)
                    imm.hideSoftInputFromWindow(
                            bindingContents.signupConfirmpwEdittext.windowToken,
                            0
                    )
                }
                userPassword != userConfirmPassword -> {
                    bindingContents.signupConfirmpwEdittext.error = getString(R.string.not_match_pw)
                    bindingContents.signupConfirmpwEdittext.isFocusableInTouchMode = true
                    bindingContents.signupConfirmpwEdittext.requestFocus()
                    imm.showSoftInput(bindingContents.signupConfirmpwEdittext, 0)
                    imm.hideSoftInputFromWindow(
                            bindingContents.signupConfirmpwEdittext.windowToken,
                            0
                    )
                }
                userName.isEmpty() -> {
                    bindingContents.signupNameEdittext.error =
                            "${getString(R.string.signname)}을 ${getString(R.string.input_fail_null)}"
                    bindingContents.signupNameEdittext.isFocusableInTouchMode = true
                    bindingContents.signupNameEdittext.requestFocus()
                    imm.showSoftInput(bindingContents.signupNameEdittext, 0)
                    imm.hideSoftInputFromWindow(bindingContents.signupNameEdittext.windowToken, 0)
                }
                else -> {
                    imm.hideSoftInputFromWindow(bindingContents.signupNameEdittext.windowToken, 0)
                    val responseListener: Response.Listener<String?> =
                            Response.Listener { response ->
                                try {
                                    val jsonObject = JSONObject(response)
                                    val success = jsonObject.getBoolean("success")
                                    if (success) {
                                        Toast.makeText(
                                                applicationContext,
                                                "$userEmail ${getString(R.string.signup)} ${getString(R.string.complete)}",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                                Intent(this@RegisterActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                                applicationContext,
                                                "${getString(R.string.signup)} ${getString(R.string.fail)}",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        return@Listener
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                            applicationContext,
                                            "${getString(R.string.error)}",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    val registerRequest =
                            RegisterRequest(
                                    userEmail,
                                    userPassword,
                                    userName,
                                    responseListener
                            )
                    val queue = Volley.newRequestQueue(this@RegisterActivity)
                    queue.add(registerRequest)
                    finish()
                }
            }
        }
        bindingContents.signupContents.movementMethod = ScrollingMovementMethod.getInstance()
    }

    private fun updateView(@LayoutRes id: Int) {
        val root = when (id) {
            R.layout.activity_regcontents -> binding.rootRegLayout
            R.layout.activity_register -> bindingContents.rootRegLayout
            else -> null
        }
        if (root != null) {
            if (root.isNotEmpty()) {
                var targetConstraintSet = ConstraintSet()
                targetConstraintSet.clone(this, id)
                targetConstraintSet.applyTo(root)
                val transitionConSet = ChangeBounds()
                transitionConSet.interpolator = AccelerateInterpolator()
                TransitionManager.beginDelayedTransition(root, transitionConSet
                )
            }
        }
    }
}