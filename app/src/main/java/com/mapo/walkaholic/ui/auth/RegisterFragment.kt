package com.mapo.walkaholic.ui.auth

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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentRegisterBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.service.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegisterBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.let { binding.viewModel = it }
        viewModel.progressBarVisibility.let { }
        binding.registerTvService.movementMethod = ScrollingMovementMethod.getInstance()
        binding.registerTvPrivacy.movementMethod = ScrollingMovementMethod.getInstance()
        binding.registerEtBirth.setText(SimpleDateFormat("yyyyMMdd").format(Date()))
        viewModel.termResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.terms = it.value.terms
                        binding.terms?.let { }
                    } else {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.err_unexpected),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Failure -> {
                }
            }
        })
        viewModel.onClickEvent.observe(
                viewLifecycleOwner,
                EventObserver(this@RegisterFragment::onClickEvent)
        )
        viewModel.registerResponse.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    when (it) {
                        is Resource.Success -> {
                            viewModel.saveAuthToken()
                            requireActivity().startNewActivity(MainActivity::class.java)
                        }
                        is Resource.Failure -> {
                        }
                    }
                })
        viewModel.getTerm()
    }

    private fun onClickEvent(name: String) {
        when (name) {
            "registerConfirm" -> {
                val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val userName: String = binding.registerEtName.text.toString().trim()
                val userNick: String = binding.registerEtNickname.text.toString().trim()
                val userBirth: String = binding.registerEtBirth.text.toString().trim()
                val userGender: String = when {
                    binding.registerChipMale.isChecked -> "0"
                    binding.registerChipFemale.isChecked -> "1"
                    else -> "-1"
                }
                val userHeight: String = binding.registerEtHeight.text.toString().trim()
                val userWeight: String = binding.registerEtWeight.text.toString().trim()
                when {
                    userName.isEmpty() || !Pattern.compile(
                            "^[a-zA-Z가-힣]*$"
                    )
                            .matcher(userName).matches() -> {
                        binding.registerEtName.error =
                                "${getString(R.string.name)}을 ${getString(R.string.err_input)}"
                        binding.registerEtName.isFocusableInTouchMode = true
                        binding.registerEtName.requestFocus()
                        imm.showSoftInput(binding.registerEtName, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtName.windowToken, 0)
                    }
                    userNick.isEmpty() || !Pattern.compile(
                            "^[0-9a-zA-Z가-힣]*$"
                    )
                            .matcher(userNick).matches() -> {
                        binding.registerEtNickname.error =
                                "${getString(R.string.nickname)}을 ${getString(R.string.err_input)}"
                        binding.registerEtNickname.isFocusableInTouchMode = true
                        binding.registerEtNickname.requestFocus()
                        imm.showSoftInput(binding.registerEtNickname, 0)
                        imm.hideSoftInputFromWindow(binding.registerEtNickname.windowToken, 0)
                    }
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
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            if (error != null) {
                                Log.e(ContentValues.TAG, "토큰 정보 보기 실패", error)
                            } else if (tokenInfo != null) {
                                viewModel.register(
                                        tokenInfo.id,
                                        userName,
                                        userNick,
                                        userBirth.toInt(),
                                        userGender.toInt(),
                                        userHeight.toInt(),
                                        userWeight.toInt()
                                )
                            }
                        }
                    }
                }
            }
            "agreeAll" -> {
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
            "agreeService" -> {
                if (binding.registerChipAgreeService.isChecked && binding.registerChipAgreePrivacy.isChecked) {
                    binding.registerChipAgreeAll.isChecked = true
                    binding.registerChipAgreeAll.performClick()
                }
            }
            "agreePrivacy" -> {
                if (binding.registerChipAgreePrivacy.isChecked && binding.registerChipAgreeService.isChecked) {
                    binding.registerChipAgreeAll.isChecked = true
                    binding.registerChipAgreeAll.performClick()
                }
            }
            "setBirth" -> {
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
                            requireContext(),
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
                                                (month + 1)
                                        )
                                    }${String.format("%02d", dayOfMonth)}"
                            )
                        }
                    }
                }
            }
            else -> null
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userPreferences = UserPreferences(requireContext())
        binding = getFragmentBinding(inflater, container)

        val sharedViewModel by viewModels<AuthViewModel> {
            ViewModelFactory(getFragmentRepository())
        }

        viewModel = sharedViewModel

        lifecycleScope.launch { userPreferences.authToken.first() }

        return binding.root
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            AuthRepository.getInstance(remoteDataSource.buildRetrofitApi(Api::class.java), userPreferences)
    //AuthRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)
}