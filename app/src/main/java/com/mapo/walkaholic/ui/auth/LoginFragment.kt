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
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.GuestApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.ui.*
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.main.MainActivity
import com.mapo.walkaholic.ui.main.dashboard.DashboardFragmentDirections
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
                    viewModel.login(token)
                    viewModel.loginResponse.observe(viewLifecycleOwner, Observer { _loginResponse ->
                        when (_loginResponse) {
                            is Resource.Success -> {
                                showToastEvent(_loginResponse.value.message.trim())
                                when (_loginResponse.value.code) {
                                    "200" -> {
                                        lifecycleScope.launch {
                                            viewModel.saveJwtToken(_loginResponse.value.data.first().token)
                                            Log.d(TAG, _loginResponse.value.data.first().token)
                                            Log.d(TAG, _loginResponse.value.message)
                                            val navDirection: NavDirections? =
                                                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(_loginResponse.value.data.first().token)
                                            if (navDirection != null) {
                                                findNavController().navigate(navDirection)
                                            }
                                        }
                                    }
                                    "401" -> {
                                        // Unauthorized Error
                                        lifecycleScope.launch {
                                            Log.d(TAG, _loginResponse.value.data.first().token)
                                            Log.d(TAG, _loginResponse.value.message)
                                            requireActivity().startNewActivity(MainActivity::class.java as Class<Activity>)
                                        }
                                    }
                                    "403" -> {
                                        // Forbidden Error
                                    }
                                    "404" -> {
                                        // Not Found Error
                                    }
                                    else -> {
                                        // Error
                                    }
                                }
                            }
                            is Resource.Loading -> {
                                // Loading
                            }
                            is Resource.Failure -> {
                                // Network Error
                                handleApiError(_loginResponse) { viewModel.login(token) }
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
                        requireActivity().startNewActivity(MainActivity::class.java as Class<Activity>)
                        /*UserApiClient.instance.logout { error ->
                            if (error != null) {
                                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                            }
                            else {
                                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                            }
                        }*/
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
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        return AuthRepository(
            remoteDataSource.buildRetrofitGuestApi(GuestApi::class.java),
            remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken, false),
            userPreferences
        )
    }
}