package com.mapo.walkaholic.ui.auth

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isNotEmpty
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.AuthApi
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentRegister1Binding
import com.mapo.walkaholic.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegister1Binding, AuthRepository>() {
    private var isAuthSMS = false
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.registerChipAgree1.movementMethod = ScrollingMovementMethod.getInstance()
        binding.registerChipAgree2.movementMethod = ScrollingMovementMethod.getInstance()
        binding.registerEtBirth.setText(SimpleDateFormat("yyyyMMdd").format(Date()))
        binding.registerChipAgreeAll.setOnClickListener {
            if (binding.registerChipAgree1.isChecked && binding.registerChipAgree2.isChecked) {
                binding.registerChipAgreeAll.isChecked = true
                binding.registerChipAgreeAll.performClick()
            }
        }
        binding.registerChipAgree2.setOnClickListener {
            if (binding.registerChipAgree2.isChecked && binding.registerChipAgree1.isChecked) {
                binding.registerChipAgreeAll.isChecked = true
                binding.registerChipAgreeAll.performClick()
            }
        }
        binding.registerChipAgreeAll.setOnClickListener {
            binding.registerChipAgree1.isClickable = false
            binding.registerChipAgree2.isClickable = false
            binding.registerChipAgreeAll.isClickable = false
            binding.registerChipAgree1.isChecked = true
            binding.registerChipAgree2.isChecked = true
            binding.registerChipAgreeAll.isChecked = true
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_register_agree),
                Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({
                updateView(R.layout.fragment_register2)
            }, 250)
        }
        binding.registerEtBirth.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imm.hideSoftInputFromWindow(binding.registerEtBirth.windowToken, 0)
                if (binding.registerEtBirth.text.toString().isEmpty() || !Pattern.compile(
                        "[1-2][0-9]{3}[0-1][0-9][0-3][0-9]"
                    )
                        .matcher(binding.registerEtBirth.text.toString()).matches()
                ) {
                    binding.registerEtBirth.setText(SimpleDateFormat("yyyyMMdd").format(Date()))
                }
                val userBirth = binding.registerEtBirth.text.toString()
                DatePickerDialog(
                    requireActivity(),
                    null,
                    String.format("%04d", userBirth.substring(0, 4).toInt()).toInt(),
                    String.format("%02d", userBirth.substring(4, 6).toInt()).toInt(),
                    String.format("%02d", userBirth.substring(6, 8).toInt()).toInt()
                ).also {
                    it.show()
                    it.setOnDateSetListener { view, year, month, dayOfMonth ->
                        binding.registerEtBirth.setText(
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
        binding.registerBtnSendSms.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (binding.registerEtPhoneNumber.text.toString().isNotEmpty() && Pattern.compile(
                    "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
                ).matcher(binding.registerEtPhoneNumber.text.toString()).matches()
            ) {
                imm.showSoftInput(binding.registerEtPhoneNumber, 0)
                imm.hideSoftInputFromWindow(binding.registerEtPhoneNumber.windowToken, 0)
                binding.registerEtPhoneNumber.error = null
                binding.registerEtPhoneNumber.isEnabled = false
                binding.registerLayoutAuthSms.visibility = View.VISIBLE
                binding.registerBtnSendSms.text = "SMS 인증번호 다시 받기"
                updateView(R.layout.fragment_register2)
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.sms_auth)}가 ${getString(R.string.msg_sms_success_send)}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.registerEtPhoneNumber.error =
                    "${getString(R.string.phone_number)}를 ${getString(R.string.err_input)}"
                binding.registerEtPhoneNumber.isFocusableInTouchMode = true
                binding.registerEtPhoneNumber.requestFocus()
                imm.showSoftInput(binding.registerEtPhoneNumber, 0)
                imm.hideSoftInputFromWindow(binding.registerEtPhoneNumber.windowToken, 0)
                isAuthSMS = false
            }
        }
        binding.registerBtnAuthSms.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (binding.registerEtSms.text.toString().isNotEmpty() && true) {
                imm.showSoftInput(binding.registerEtSms, 0)
                imm.hideSoftInputFromWindow(binding.registerEtSms.windowToken, 0)
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.phone_number)} 인증 ${getString(R.string.err_complete)}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.registerBtnSendSms.isClickable = false
                binding.registerEtSms.error = null
                binding.registerEtSms.isEnabled = false
                binding.registerBtnAuthSms.isClickable = false
                updateView(R.layout.fragment_register2)
                isAuthSMS = true
            } else {
                binding.registerEtSms.error =
                    "${getString(R.string.phone_number)}를 ${getString(R.string.err_input)}"
                binding.registerEtSms.isFocusableInTouchMode = true
                binding.registerEtSms.requestFocus()
                imm.showSoftInput(binding.registerEtSms, 0)
                imm.hideSoftInputFromWindow(binding.registerEtSms.windowToken, 0)
                isAuthSMS = false
            }
        }
        binding.registerBtnNext.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userID: String = binding.registerEtUserId.text.toString()
            val userPassword: String = binding.registerEtUserPassword.text.toString()
            val userConfirmPassword: String =
                binding.registerEtUserPasswordConfirm.text.toString()
            val userName: String = binding.registerEtName.text.toString()
            val userNick: String = binding.registerEtNickname.text.toString()
            val userPhone: String = binding.registerEtPhoneNumber.text.toString()
            val userSMSAuth: String = binding.registerEtSms.text.toString()
            when {
                userID.isEmpty() -> {
                    binding.registerEtUserId.error =
                        "${getString(R.string.userid)}을 ${getString(R.string.err_input)}"
                    binding.registerEtUserId.isFocusableInTouchMode = true
                    binding.registerEtUserId.requestFocus()
                    imm.showSoftInput(binding.registerEtUserId, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtUserId.windowToken, 0)
                }
                userPassword.isEmpty() -> {
                    binding.registerEtUserPassword.error =
                        "${getString(R.string.password)}를 ${getString(R.string.err_input)}"
                    binding.registerEtUserPassword.isFocusableInTouchMode = true
                    binding.registerEtUserPassword.requestFocus()
                    imm.showSoftInput(binding.registerEtUserPassword, 0)
                    imm.hideSoftInputFromWindow(
                        binding.registerEtUserPassword.windowToken,
                        0
                    )
                }
                userConfirmPassword.isEmpty() -> {
                    binding.registerEtUserPasswordConfirm.error =
                        "${getString(R.string.password_confirm)}을 ${getString(R.string.err_input)}"
                    binding.registerEtUserPasswordConfirm.isFocusableInTouchMode = true
                    binding.registerEtUserPasswordConfirm.requestFocus()
                    imm.showSoftInput(binding.registerEtUserPasswordConfirm, 0)
                    imm.hideSoftInputFromWindow(
                        binding.registerEtUserPasswordConfirm.windowToken,
                        0
                    )
                }
                userPassword != userConfirmPassword -> {
                    binding.registerEtUserPasswordConfirm.error = getString(R.string.err_not_match_password)
                    binding.registerEtUserPasswordConfirm.isFocusableInTouchMode = true
                    binding.registerEtUserPasswordConfirm.requestFocus()
                    imm.showSoftInput(binding.registerEtUserPasswordConfirm, 0)
                    imm.hideSoftInputFromWindow(
                        binding.registerEtUserPasswordConfirm.windowToken,
                        0
                    )
                }
                userName.isEmpty() -> {
                    binding.registerEtName.error =
                        "${getString(R.string.name)}을 ${getString(R.string.err_input)}"
                    binding.registerEtName.isFocusableInTouchMode = true
                    binding.registerEtName.requestFocus()
                    imm.showSoftInput(binding.registerEtName, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtName.windowToken, 0)
                }
                userNick.isEmpty() -> {
                    binding.registerEtNickname.error =
                        "${getString(R.string.nickname)}을 ${getString(R.string.err_input)}"
                    binding.registerEtNickname.isFocusableInTouchMode = true
                    binding.registerEtNickname.requestFocus()
                    imm.showSoftInput(binding.registerEtNickname, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtNickname.windowToken, 0)
                }
                userPhone.isEmpty() || !Pattern.compile(
                    "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
                )
                    .matcher(userPhone).matches() -> {
                    binding.registerEtPhoneNumber.error =
                        "${getString(R.string.phone_number)}를 ${getString(R.string.err_input)}"
                    binding.registerEtPhoneNumber.isFocusableInTouchMode = true
                    binding.registerEtPhoneNumber.requestFocus()
                    imm.showSoftInput(binding.registerEtPhoneNumber, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtPhoneNumber.windowToken, 0)
                }
                userSMSAuth.isEmpty() || !isAuthSMS -> {
                    if (binding.registerLayoutAuthSms.visibility == View.VISIBLE) {
                        binding.registerEtSms.error =
                            "${getString(R.string.phone_number)} 인증을 ${getString(R.string.err_complete)}"
                        binding.registerEtSms.isFocusableInTouchMode = true
                        binding.registerEtSms.requestFocus()
                        imm.showSoftInput(binding.registerEtSms, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtSms.windowToken, 0)
                    } else {
                        imm.showSoftInput(binding.registerEtPhoneNumber, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtPhoneNumber.windowToken, 0)
                        Toast.makeText(
                            requireContext(),
                            "${getString(R.string.phone_number)} 인증을 ${getString(R.string.err_input)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {
                    updateView(R.layout.fragment_register3)
                }
            }
            binding.registerBtnRegister.setOnClickListener {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val userBirth: String = binding.registerEtBirth.text.toString()
                val userGender: String = when {
                    binding.registerChipMale.isChecked -> "0"
                    binding.registerChipFemale.isChecked -> "1"
                    else -> "-1"
                }
                val userHeight: String = binding.registerEtHeight.text.toString()
                val userWeight: String = binding.registerEtWeight.text.toString()
                when {
                    userBirth.isEmpty() || !Pattern.compile(
                        "[1-2][0-9]{3}[0-1][0-9][0-3][0-9]"
                    )
                        .matcher(userBirth).matches() -> {
                        binding.registerEtBirth.error =
                            "${getString(R.string.birth)}을 ${getString(R.string.err_input)}"
                        binding.registerEtBirth.isFocusableInTouchMode = true
                        binding.registerEtBirth.requestFocus()
                        imm.showSoftInput(binding.registerEtBirth, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtBirth.windowToken, 0)
                    }
                    userGender.toInt() != 0 && userGender.toInt() != 1 -> {
                        Toast.makeText(
                            requireContext(),
                            "${getString(R.string.gender)}을 ${getString(R.string.err_input)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    userHeight.isEmpty() -> {
                        binding.registerEtHeight.error =
                            "${getString(R.string.height)}를 ${getString(R.string.err_input)}"
                        binding.registerEtHeight.isFocusableInTouchMode = true
                        binding.registerEtHeight.requestFocus()
                        imm.showSoftInput(binding.registerEtHeight, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtHeight.windowToken, 0)
                    }
                    userWeight.isEmpty() -> {
                        binding.registerEtWeight.error =
                            "${getString(R.string.weight)}를 ${getString(R.string.err_input)}"
                        binding.registerEtWeight.isFocusableInTouchMode = true
                        binding.registerEtWeight.requestFocus()
                        imm.showSoftInput(binding.registerEtWeight, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtWeight.windowToken, 0)
                    }
                    else -> {
                        /*
                        imm.hideSoftInputFromWindow(binding.registerEtName.windowToken, 0)
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
                                            Intent(this@RegisterFragment, LoginFragment::class.java)
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
                        val queue = Volley.newRequestQueue(this@RegisterFragment)
                        queue.add(registerRequest)
                        */
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
                targetConstraintSet.clone(requireActivity(), id)
                targetConstraintSet.applyTo(root)
                val transitionConSet = ChangeBounds()
                transitionConSet.interpolator = AccelerateInterpolator()
                TransitionManager.beginDelayedTransition(root, transitionConSet)
            }
        }

    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegister1Binding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)
}