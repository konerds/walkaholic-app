package com.mapo.walkaholic.ui.auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.ui.*
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        lifecycleScope.launch {
            viewModel.saveIsFirst()
        }
        viewModel.getFilenameTitleLogo()
        viewModel.filenameLogoImageResponse.observe(viewLifecycleOwner, Observer { _filenameLogoImageResponse ->
            when(_filenameLogoImageResponse) {
                is Resource.Success -> {
                    when(_filenameLogoImageResponse.value.code) {
                        "200" -> {
                            binding.filenameLogoImage = _filenameLogoImageResponse.value.data.first()
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
        binding.loginBtnKakao.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    viewModel.showToastEvent(GlobalApplication.getGlobalApplicationContext()
                        .getString(R.string.err_auth))
                } else if (token != null) {
                    viewModel.showToastEvent(GlobalApplication.getGlobalApplicationContext()
                        .getString(R.string.msg_success_auth))
                    viewModel.login()
                    viewModel.loginResponse.observe(viewLifecycleOwner, Observer { _loginResponse ->
                        when (_loginResponse) {
                            is Resource.Success -> {
                                when (_loginResponse.value.code) {
                                    "200" -> {
                                        lifecycleScope.launch {
                                            Toast.makeText(
                                                requireContext(),
                                                _loginResponse.value.message.trim(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            /*viewModel.saveJwtToken(it.value.jwtToken)*/
                                            // Log.d(TAG, it.value.jwtToken)
                                            Log.d(TAG, _loginResponse.value.message)
                                            requireActivity().startNewActivity(MainActivity::class.java as Class<Activity>)
                                        }
                                    }
                                    else -> {
                                        confirmDialog(
                                            _loginResponse.value.message,
                                            {
                                                viewModel.login()
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
                                handleApiError(_loginResponse) { viewModel.login() }
                            }
                        }
                    })
                    //viewModel.login(token)
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(GlobalApplication.getGlobalApplicationContext())) {
                UserApiClient.instance.loginWithKakaoTalk(
                    GlobalApplication.getGlobalApplicationContext(),
                    callback = callback
                )
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    GlobalApplication.getGlobalApplicationContext(),
                    callback = callback
                )
            }
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