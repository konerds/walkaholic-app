package com.mapo.walkaholic.ui.auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.ui.*
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.let { binding.viewModel = it }
        lifecycleScope.launch {
            viewModel.saveIsFirst()
        }
        viewModel.filenameLogoTitle.observe(viewLifecycleOwner, Observer {
            setImageUrl(binding.loginIvLogo, it)
        })
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    when (it.value.code) {
                        "200" -> {
                            lifecycleScope.launch {
                                Toast.makeText(
                                    requireContext(),
                                    it.value.message.trim(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                /*viewModel.saveJwtToken(it.value.jwtToken)*/
                                // Log.d(TAG, it.value.jwtToken)
                                requireActivity().startNewActivity(MainActivity::class.java as Class<Activity>)
                            }
                        }
                        "400" -> {
                            requireView().findNavController()
                                .navigate(R.id.action_loginFragment_to_registerFragment)
                        }
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                it.value.message.trim(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        it.errorBody.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    /*handleApiError(it) { viewModel.login() }*/
                }
            }
        })
        binding.loginBtnKakao.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    viewModel.showToastEvent(
                        GlobalApplication.getGlobalApplicationContext()
                            .getString(R.string.err_auth)
                    )
                } else if (token != null) {
                    viewModel.showToastEvent(
                        GlobalApplication.getGlobalApplicationContext()
                            .getString(R.string.msg_success_auth)
                    )
                    viewModel.login()
                    //viewModel.login(token)
                }
            }
            viewModel.getFilenameTitleLogo()
            viewModel.getAuth(callback)
        }
        binding.loginBtnTutorial.setOnClickListener {
            requireActivity().startNewActivity(GuideActivity::class.java as Class<Activity>)
        }
    }

    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository {
        return AuthRepository(
            remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java),
            userPreferences
        )
    }
}