package com.mapo.walkaholic.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentRegisterBinding
import com.mapo.walkaholic.ui.base.BaseFragment

class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegisterBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.registerBtnRegister.setOnClickListener {
            val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userName: String = binding.registerEtName.text.toString().trim()
            val userNick: String = binding.registerEtNickname.text.toString().trim()
            val userHeight: String = binding.registerEtHeight.text.toString().trim()
            val userWeight: String = binding.registerEtWeight.text.toString().trim()
            when {
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
                    //@TODO OAuth Create Account
                }
            }
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            AuthRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)
}