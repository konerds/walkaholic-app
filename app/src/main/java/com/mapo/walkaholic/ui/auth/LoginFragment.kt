package com.mapo.walkaholic.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.startNewActivity
import com.mapo.walkaholic.ui.visible

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.loginProgressBar.visible(false)
        binding.loginBtnTutorial.setOnClickListener {
            requireActivity().startNewActivity(GuideActivity::class.java)
        }

        binding.loginBtnKakao.setOnClickListener {
            binding.loginProgressBar.visible(true)
            // @TODO OAuth Login Process
            viewModel.login()
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            AuthRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)
}