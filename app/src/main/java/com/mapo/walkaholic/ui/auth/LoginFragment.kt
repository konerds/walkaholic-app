package com.mapo.walkaholic.ui.auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
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
                    viewModel.loginResponse.observe(viewLifecycleOwner, Observer { _loginResponse ->
                        when (_loginResponse) {
                            is Resource.Success -> {
                                when (_loginResponse.value.code) {
                                    "200" -> {
                                        lifecycleScope.launch {
                                            showToastEvent(_loginResponse.value.message.trim())
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
                }
            }
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _tokenInfo, _error ->
                    if (_error != null) {
                        if (_error is KakaoSdkError && _error.isInvalidTokenError()) {
                            // Need Login
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
                        } else {
                            // Error
                        }
                    } else {
                        // Token Existed (If need, Refresh)

                        /*UserApiClient.instance.logout { error ->
                            if (error != null) {
                                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                            }
                            else {
                                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                            }
                        }*/

                        viewModel.login()
                        viewModel.loginResponse.observe(viewLifecycleOwner, Observer { _loginResponse ->
                            when (_loginResponse) {
                                is Resource.Success -> {
                                    when (_loginResponse.value.code) {
                                        "200" -> {
                                            lifecycleScope.launch {
                                                showToastEvent(_loginResponse.value.message.trim())
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
                    }
                }
            } else {
                // Need Login
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
        }
        binding.loginBtnTutorial.setOnClickListener {
            requireActivity().startNewActivity(GuideActivity::class.java as Class<Activity>)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmDialog(getString(com.mapo.walkaholic.R.string.err_deny_prev), null, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
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