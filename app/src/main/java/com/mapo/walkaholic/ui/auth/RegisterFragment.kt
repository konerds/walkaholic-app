package com.mapo.walkaholic.ui.auth

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isNotEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentRegisterBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterFragment :
    BaseFragment<RegisterViewModel, FragmentRegisterBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.progressBarVisibility.let { }
        binding.registerTvService.movementMethod = ScrollingMovementMethod.getInstance()
        binding.registerTvPrivacy.movementMethod = ScrollingMovementMethod.getInstance()
        binding.registerEtBirth.setText(SimpleDateFormat("yyyy-MM-dd").format(Date()))
        viewModel.getFilenameTitleLogo()
        viewModel.filenameLogoImageResponse.observe(
            viewLifecycleOwner,
            Observer { _filenameLogoImageResponse ->
                when (_filenameLogoImageResponse) {
                    is Resource.Success -> {
                        when (_filenameLogoImageResponse.value.code) {
                            "200" -> {
                                binding.filenameLogoImage =
                                    _filenameLogoImageResponse.value.data.first()
                            }
                            else -> {
                                // Error
                                confirmDialog(
                                    _filenameLogoImageResponse.value.message,
                                    {
                                        viewModel.getFilenameTitleLogo()
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
                        handleApiError(_filenameLogoImageResponse) { viewModel.getFilenameTitleLogo() }
                    }
                }
            })
        viewModel.getTermService()
        viewModel.termServiceResponse.observe(viewLifecycleOwner, Observer { _termServiceResponse ->
            when (_termServiceResponse) {
                is Resource.Success -> {
                    when (_termServiceResponse.value.code) {
                        "200" -> {
                            binding.termService = _termServiceResponse.value.data.first()
                        }
                        else -> {
                            confirmDialog(
                                _termServiceResponse.value.message,
                                {
                                    viewModel.getTermService()
                                },
                                "재시도"
                            )
                        }
                    }
                }
                is Resource.Failure -> {
                    handleApiError(_termServiceResponse) { viewModel.getTermService() }
                }
            }
        })
        viewModel.getTermPrivacy()
        viewModel.termPrivacyResponse.observe(viewLifecycleOwner, Observer { _termPrivacyResponse ->
            when (_termPrivacyResponse) {
                is Resource.Success -> {
                    when (_termPrivacyResponse.value.code) {
                        "200" -> {
                            binding.termPrivacy = _termPrivacyResponse.value.data.first()
                        }
                        else -> {
                            confirmDialog(
                                _termPrivacyResponse.value.message,
                                {
                                    viewModel.getTermPrivacy()
                                },
                                "재시도"
                            )
                        }
                    }
                }
                is Resource.Failure -> {
                    handleApiError(_termPrivacyResponse) { viewModel.getTermPrivacy() }
                }
            }
        })
        binding.registerChipAgreeAll.setOnClickListener {
            binding.registerChipAgreeService.isClickable = false
            binding.registerChipAgreePrivacy.isClickable = false
            binding.registerChipAgreeAll.isClickable = false
            binding.registerChipAgreeService.isChecked = true
            binding.registerChipAgreePrivacy.isChecked = true
            binding.registerChipAgreeAll.isChecked = true
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_register_agree),
                Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({
                val root = binding.rootRegLayout
                if (root.isNotEmpty()) {
                    var targetConstraintSet = ConstraintSet()
                    targetConstraintSet.clone(requireContext(), R.layout.fragment_register)
                    targetConstraintSet.setVisibility(R.id.registerLayout1, View.GONE)
                    targetConstraintSet.setVisibility(R.id.registerLayout2, View.VISIBLE)
                    targetConstraintSet.applyTo(root)
                    val transitionConSet = ChangeBounds()
                    transitionConSet.interpolator = AccelerateInterpolator()
                    TransitionManager.beginDelayedTransition(root, transitionConSet)
                }
            }, 250)
        }
        binding.registerChipAgreeService.setOnClickListener {
            if (binding.registerChipAgreeService.isChecked && binding.registerChipAgreePrivacy.isChecked) {
                binding.registerChipAgreeAll.isChecked = true
                binding.registerChipAgreeAll.performClick()
            }
        }
        binding.registerChipAgreeService.setOnClickListener {
            if (binding.registerChipAgreePrivacy.isChecked && binding.registerChipAgreeService.isChecked) {
                binding.registerChipAgreeAll.isChecked = true
                binding.registerChipAgreeAll.performClick()
            }
        }
        binding.registerEtBirth.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imm.hideSoftInputFromWindow(binding.registerEtBirth.windowToken, 0)
                if (binding.registerEtBirth.text.toString().isEmpty() || !Pattern.compile(
                        "[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]"
                    )
                        .matcher(binding.registerEtBirth.text.toString()).matches()
                ) {
                    binding.registerEtBirth.setText(SimpleDateFormat("yyyy-MM-dd").format(Date()))
                }
                val userBirth = binding.registerEtBirth.text.toString()
                DatePickerDialog(
                    requireContext(),
                    null,
                    String.format("%04d", userBirth.substring(0, 4).toInt()).toInt(),
                    String.format("%02d", userBirth.substring(5, 7).toInt()).toInt(),
                    String.format("%02d", userBirth.substring(8, 10).toInt()).toInt()
                ).also {
                    it.show()
                    it.setOnDateSetListener { view, year, month, dayOfMonth ->
                        binding.registerEtBirth.setText(
                            "${String.format("%04d", year)}-${
                                String.format(
                                    "%02d",
                                    (month + 1)
                                )
                            }-${String.format("%02d", dayOfMonth)}"
                        )
                    }
                }
            }
        }
        binding.registerBtnRegister.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userNickname: String = binding.registerEtNickname.text.toString().trim()
            val userBirth: String = binding.registerEtBirth.text.toString().trim()
            val userGender: String = when {
                binding.registerChipMale.isChecked -> "남"
                binding.registerChipFemale.isChecked -> "여"
                else -> "-1"
            }
            val userHeight: String = binding.registerEtHeight.text.toString().trim()
            val userWeight: String = binding.registerEtWeight.text.toString().trim()
            when {
                userNickname.isEmpty() || !Pattern.compile(
                    "^[0-9a-zA-Z가-힣]*$"
                )
                    .matcher(userNickname).matches() -> {
                    binding.registerEtNickname.error =
                        "${getString(R.string.nickname)}을 ${getString(R.string.err_input)}"
                    binding.registerEtNickname.isFocusableInTouchMode = true
                    binding.registerEtNickname.requestFocus()
                    imm.showSoftInput(binding.registerEtNickname, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtNickname.windowToken, 0)
                }
                userBirth.isEmpty() || !Pattern.compile(
                    "[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]"
                )
                    .matcher(userBirth).matches() -> {
                    binding.registerEtBirth.error =
                        "${getString(R.string.birth)}을 ${getString(R.string.err_input)}"
                    binding.registerEtBirth.isFocusableInTouchMode = true
                    binding.registerEtBirth.requestFocus()
                    imm.showSoftInput(binding.registerEtBirth, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtBirth.windowToken, 0)
                }
                userGender != "남" && userGender != "여" -> {
                    Toast.makeText(
                        requireContext(),
                        "${getString(R.string.gender)}을 ${getString(R.string.err_input)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                userHeight.isEmpty() || (userHeight.toInt() <= 0) -> {
                    binding.registerEtHeight.error =
                        "${getString(R.string.height)}를 ${getString(R.string.err_input)}"
                    binding.registerEtHeight.isFocusableInTouchMode = true
                    binding.registerEtHeight.requestFocus()
                    imm.showSoftInput(binding.registerEtHeight, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtHeight.windowToken, 0)
                }
                userWeight.isEmpty() || (userWeight.toInt() <= 0) -> {
                    binding.registerEtWeight.error =
                        "${getString(R.string.weight)}를 ${getString(R.string.err_input)}"
                    binding.registerEtWeight.isFocusableInTouchMode = true
                    binding.registerEtWeight.requestFocus()
                    imm.showSoftInput(binding.registerEtWeight, 0)
                    imm.hideSoftInputFromWindow(binding.registerEtWeight.windowToken, 0)
                }
                else -> {
                    viewModel.register(
                        userBirth,
                        userGender,
                        userHeight,
                        userNickname,
                        userWeight
                    )
                    viewModel.registerResponse.observe(
                        viewLifecycleOwner,
                        Observer { _registerResponse ->
                            when (_registerResponse) {
                                is Resource.Success -> {
                                    when (_registerResponse.value.code) {
                                        "200" -> {
                                            lifecycleScope.launch {
                                                /*viewModel.saveJwtToken(it.value.jwtToken)*/
                                                requireActivity().startNewActivity(MainActivity::class.java)
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _registerResponse.value.message,
                                                {
                                                    viewModel.register(
                                                        userBirth,
                                                        userGender,
                                                        userHeight,
                                                        userNickname,
                                                        userWeight
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Failure -> {
                                    handleApiError(_registerResponse) {
                                        viewModel.register(
                                            userBirth,
                                            userGender,
                                            userHeight,
                                            userNickname,
                                            userWeight
                                        )
                                    }
                                }
                            }
                        })
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navDirection: NavDirections? =
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                if (navDirection != null) {
                    findNavController().navigate(navDirection)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun getViewModel() = RegisterViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository {
        return AuthRepository(
            remoteDataSource.buildRetrofitInnerApi(GuestApi::class.java),
            userPreferences
        )
    }
}