package com.mapo.walkaholic.view

import android.app.DatePickerDialog
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
import androidx.core.view.isNotEmpty
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.mapo.walkaholic.R
import com.mapo.walkaholic.model.RegisterRequest
import com.mapo.walkaholic.databinding.ActivityRegcontents1Binding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.M)
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegcontents1Binding
    private var isAuthSMS = false
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
        binding = ActivityRegcontents1Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.signupContents.movementMethod = ScrollingMovementMethod.getInstance()
        binding.signupContents2.movementMethod = ScrollingMovementMethod.getInstance()
        binding.signupBirthEdittext.setText(SimpleDateFormat("yyyyMMdd").format(Date()))
        binding.signupAgreea.setOnClickListener {
            if (binding.signupAgreea.isChecked && binding.signupAgreeb.isChecked) {
                binding.signupAgreeall.isChecked = true
                binding.signupAgreeall.performClick()
            }
        }
        binding.signupAgreeb.setOnClickListener {
            if (binding.signupAgreeb.isChecked && binding.signupAgreea.isChecked) {
                binding.signupAgreeall.isChecked = true
                binding.signupAgreeall.performClick()
            }
        }
        binding.signupAgreeall.setOnClickListener {
            binding.signupAgreea.isClickable = false
            binding.signupAgreeb.isClickable = false
            binding.signupAgreeall.isClickable = false
            binding.signupAgreea.isChecked = true
            binding.signupAgreeb.isChecked = true
            binding.signupAgreeall.isChecked = true
            Toast.makeText(
                    applicationContext,
                    getString(R.string.signup_agree),
                    Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({
                updateView(R.layout.activity_regcontents2)
            }, 250)
        }
        binding.signupBirthEdittext.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imm.hideSoftInputFromWindow(binding.signupBirthEdittext.windowToken, 0)
                if (binding.signupBirthEdittext.text.toString().isEmpty() || !Pattern.compile(
                                "[1-2][0-9]{3}[0-1][0-9][0-3][0-9]"
                        )
                                .matcher(binding.signupBirthEdittext.text.toString()).matches()
                ) {
                    binding.signupBirthEdittext.setText(SimpleDateFormat("yyyyMMdd").format(Date()))
                }
                val userBirth = binding.signupBirthEdittext.text.toString()
                DatePickerDialog(
                        this,
                        null,
                        String.format("%04d", userBirth.substring(0, 4).toInt()).toInt(),
                        String.format("%02d", userBirth.substring(4, 6).toInt()).toInt(),
                        String.format("%02d", userBirth.substring(6, 8).toInt()).toInt()
                ).also {
                    it.show()
                    it.setOnDateSetListener { view, year, month, dayOfMonth ->
                        binding.signupBirthEdittext.setText(
                                "${String.format("%04d", year)}${
                                    String.format(
                                            "%02d",
                                            month
                                    )
                                }${String.format("%02d", dayOfMonth)}"
                        )
                    }
                }
            }
        }
        binding.signupSmsButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (binding.signupPhoneEdittext.text.toString().isNotEmpty() && Pattern.compile(
                            "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
                    ).matcher(binding.signupPhoneEdittext.text.toString()).matches()
            ) {
                imm.showSoftInput(binding.signupPhoneEdittext, 0)
                imm.hideSoftInputFromWindow(binding.signupPhoneEdittext.windowToken, 0)
                binding.signupPhoneEdittext.error = null
                binding.signupPhoneEdittext.isEnabled = false
                binding.layoutAuthsms.visibility = View.VISIBLE
                binding.signupSmsButton.text = "SMS 인증번호 다시 받기"
                updateView(R.layout.activity_regcontents2)
                Toast.makeText(
                        applicationContext,
                        "${getString(R.string.signsmsrauth)}가 ${getString(R.string.sms_success_send)}",
                        Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.signupPhoneEdittext.error =
                        "${getString(R.string.signphone)}를 ${getString(R.string.input_fail_null)}"
                binding.signupPhoneEdittext.isFocusableInTouchMode = true
                binding.signupPhoneEdittext.requestFocus()
                imm.showSoftInput(binding.signupPhoneEdittext, 0)
                imm.hideSoftInputFromWindow(binding.signupPhoneEdittext.windowToken, 0)
                isAuthSMS = false
            }
        }
        binding.signupAuthButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (binding.signupSmsEdittext.text.toString().isNotEmpty() && true) {
                imm.showSoftInput(binding.signupSmsEdittext, 0)
                imm.hideSoftInputFromWindow(binding.signupSmsEdittext.windowToken, 0)
                Toast.makeText(
                        applicationContext,
                        "${getString(R.string.signphone)} 인증 ${getString(R.string.complete)}",
                        Toast.LENGTH_SHORT
                ).show()
                binding.signupSmsButton.isClickable = false
                binding.signupSmsEdittext.error = null
                binding.signupSmsEdittext.isEnabled = false
                binding.signupAuthButton.isClickable = false
                updateView(R.layout.activity_regcontents2)
                isAuthSMS = true
            } else {
                binding.signupSmsEdittext.error =
                        "${getString(R.string.signphone)}를 ${getString(R.string.input_fail_null)}"
                binding.signupSmsEdittext.isFocusableInTouchMode = true
                binding.signupSmsEdittext.requestFocus()
                imm.showSoftInput(binding.signupSmsEdittext, 0)
                imm.hideSoftInputFromWindow(binding.signupSmsEdittext.windowToken, 0)
                isAuthSMS = false
            }
        }
        binding.signupSignupnextButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userID: String = binding.signupUseridEdittext.text.toString()
            val userPassword: String = binding.signupPasswordEdittext.text.toString()
            val userConfirmPassword: String =
                    binding.signupConfirmpwEdittext.text.toString()
            val userName: String = binding.signupNameEdittext.text.toString()
            val userNick: String = binding.signupNicknameEdittext.text.toString()
            val userPhone: String = binding.signupPhoneEdittext.text.toString()
            val userSMSAuth: String = binding.signupSmsEdittext.text.toString()
            when {
                userID.isEmpty() -> {
                    binding.signupUseridEdittext.error =
                            "${getString(R.string.userid)}을 ${getString(R.string.input_fail_null)}"
                    binding.signupUseridEdittext.isFocusableInTouchMode = true
                    binding.signupUseridEdittext.requestFocus()
                    imm.showSoftInput(binding.signupUseridEdittext, 0)
                    imm.hideSoftInputFromWindow(binding.signupUseridEdittext.windowToken, 0)
                }
                userPassword.isEmpty() -> {
                    binding.signupPasswordEdittext.error =
                            "${getString(R.string.password)}를 ${getString(R.string.input_fail_null)}"
                    binding.signupPasswordEdittext.isFocusableInTouchMode = true
                    binding.signupPasswordEdittext.requestFocus()
                    imm.showSoftInput(binding.signupPasswordEdittext, 0)
                    imm.hideSoftInputFromWindow(
                            binding.signupPasswordEdittext.windowToken,
                            0
                    )
                }
                userConfirmPassword.isEmpty() -> {
                    binding.signupConfirmpwEdittext.error =
                            "${getString(R.string.confirmpw)}을 ${getString(R.string.input_fail_null)}"
                    binding.signupConfirmpwEdittext.isFocusableInTouchMode = true
                    binding.signupConfirmpwEdittext.requestFocus()
                    imm.showSoftInput(binding.signupConfirmpwEdittext, 0)
                    imm.hideSoftInputFromWindow(
                            binding.signupConfirmpwEdittext.windowToken,
                            0
                    )
                }
                userPassword != userConfirmPassword -> {
                    binding.signupConfirmpwEdittext.error = getString(R.string.not_match_pw)
                    binding.signupConfirmpwEdittext.isFocusableInTouchMode = true
                    binding.signupConfirmpwEdittext.requestFocus()
                    imm.showSoftInput(binding.signupConfirmpwEdittext, 0)
                    imm.hideSoftInputFromWindow(
                            binding.signupConfirmpwEdittext.windowToken,
                            0
                    )
                }
                userName.isEmpty() -> {
                    binding.signupNameEdittext.error =
                            "${getString(R.string.signname)}을 ${getString(R.string.input_fail_null)}"
                    binding.signupNameEdittext.isFocusableInTouchMode = true
                    binding.signupNameEdittext.requestFocus()
                    imm.showSoftInput(binding.signupNameEdittext, 0)
                    imm.hideSoftInputFromWindow(binding.signupNameEdittext.windowToken, 0)
                }
                userNick.isEmpty() -> {
                    binding.signupNicknameEdittext.error =
                            "${getString(R.string.signnick)}을 ${getString(R.string.input_fail_null)}"
                    binding.signupNicknameEdittext.isFocusableInTouchMode = true
                    binding.signupNicknameEdittext.requestFocus()
                    imm.showSoftInput(binding.signupNicknameEdittext, 0)
                    imm.hideSoftInputFromWindow(binding.signupNicknameEdittext.windowToken, 0)
                }
                userPhone.isEmpty() || !Pattern.compile(
                        "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
                )
                        .matcher(userPhone).matches() -> {
                    binding.signupPhoneEdittext.error =
                            "${getString(R.string.signphone)}를 ${getString(R.string.input_fail_null)}"
                    binding.signupPhoneEdittext.isFocusableInTouchMode = true
                    binding.signupPhoneEdittext.requestFocus()
                    imm.showSoftInput(binding.signupPhoneEdittext, 0)
                    imm.hideSoftInputFromWindow(binding.signupPhoneEdittext.windowToken, 0)
                }
                userSMSAuth.isEmpty() || !isAuthSMS -> {
                    if (binding.layoutAuthsms.visibility == View.VISIBLE) {
                        binding.signupSmsEdittext.error =
                                "${getString(R.string.signphone)} 인증을 ${getString(R.string.input_fail_complete)}"
                        binding.signupSmsEdittext.isFocusableInTouchMode = true
                        binding.signupSmsEdittext.requestFocus()
                        imm.showSoftInput(binding.signupSmsEdittext, 0)
                        imm.hideSoftInputFromWindow(binding.signupSmsEdittext.windowToken, 0)
                    } else {
                        imm.showSoftInput(binding.signupPhoneEdittext, 0)
                        imm.hideSoftInputFromWindow(binding.signupPhoneEdittext.windowToken, 0)
                        Toast.makeText(
                                applicationContext,
                                "${getString(R.string.signphone)} 인증을 ${getString(R.string.input_fail_complete)}",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {
                    updateView(R.layout.activity_regcontents3)
                }
            }
            binding.signupSignupButton.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val userBirth: String = binding.signupBirthEdittext.text.toString()
                val userGender: String = when {
                    binding.signupGenmail.isChecked -> "0"
                    binding.signupGenfemale.isChecked -> "1"
                    else -> "-1"
                }
                val userHeight: String = binding.signupHeightEdittext.text.toString()
                val userWeight: String = binding.signupWeightEdittext.text.toString()
                when {
                    userBirth.isEmpty() || !Pattern.compile(
                            "[1-2][0-9]{3}[0-1][0-9][0-3][0-9]"
                    )
                            .matcher(userBirth).matches() -> {
                        binding.signupBirthEdittext.error =
                                "${getString(R.string.signbirth)}을 ${getString(R.string.input_fail_null)}"
                        binding.signupBirthEdittext.isFocusableInTouchMode = true
                        binding.signupBirthEdittext.requestFocus()
                        imm.showSoftInput(binding.signupBirthEdittext, 0)
                        imm.hideSoftInputFromWindow(binding.signupBirthEdittext.windowToken, 0)
                    }
                    userGender.toInt() != 0 && userGender.toInt() != 1 -> {
                        Toast.makeText(
                                applicationContext,
                                "${getString(R.string.signgender)}을 ${getString(R.string.input_fail_null)}",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    userHeight.isEmpty() -> {
                        binding.signupHeightEdittext.error =
                                "${getString(R.string.signheight)}를 ${getString(R.string.input_fail_null)}"
                        binding.signupHeightEdittext.isFocusableInTouchMode = true
                        binding.signupHeightEdittext.requestFocus()
                        imm.showSoftInput(binding.signupHeightEdittext, 0)
                        imm.hideSoftInputFromWindow(binding.signupHeightEdittext.windowToken, 0)
                    }
                    userWeight.isEmpty() -> {
                        binding.signupWeightEdittext.error =
                                "${getString(R.string.signweight)}를 ${getString(R.string.input_fail_null)}"
                        binding.signupWeightEdittext.isFocusableInTouchMode = true
                        binding.signupWeightEdittext.requestFocus()
                        imm.showSoftInput(binding.signupWeightEdittext, 0)
                        imm.hideSoftInputFromWindow(binding.signupWeightEdittext.windowToken, 0)
                    }
                    else -> {
                        imm.hideSoftInputFromWindow(binding.signupNameEdittext.windowToken, 0)
                        val responseListener: Response.Listener<String?> =
                                Response.Listener { response ->
                                    try {
                                        val jsonObject = JSONObject(response)
                                        val success = jsonObject.getBoolean("success")
                                        if (success) {
                                            Toast.makeText(
                                                    applicationContext,
                                                    "$userID ${getString(R.string.signup)} ${getString(R.string.complete)}",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                            val intent =
                                                    Intent(this@RegisterActivity, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                    applicationContext,
                                                    "${getString(R.string.signup)} ${getString(R.string.fail)}\n${
                                                        jsonObject.getString(
                                                                "error"
                                                        )
                                                    }",
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
                                        userID,
                                        userPassword,
                                        userName,
                                        userNick,
                                        userPhone,
                                        userBirth,
                                        userGender,
                                        userHeight,
                                        userWeight,
                                        responseListener
                                )
                        val queue = Volley.newRequestQueue(this@RegisterActivity)
                        queue.add(registerRequest)
                    }
                }
            }
        }
    }

    private fun updateView(@LayoutRes id: Int) {
        val root = binding.rootRegLayout
        if (root != null) {
            if (root.isNotEmpty()) {
                var targetConstraintSet = ConstraintSet()
                targetConstraintSet.clone(this, id)
                targetConstraintSet.applyTo(root)
                val transitionConSet = ChangeBounds()
                transitionConSet.interpolator = AccelerateInterpolator()
                TransitionManager.beginDelayedTransition(root, transitionConSet)
            }
        }
    }
}