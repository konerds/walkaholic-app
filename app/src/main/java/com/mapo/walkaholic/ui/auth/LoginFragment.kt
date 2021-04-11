package com.mapo.walkaholic.ui.auth

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.service.DashboardFragment
import com.mapo.walkaholic.ui.service.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import com.mapo.walkaholic.ui.visible

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.loginProgressBar.visible(false)
        binding.loginBtnTutorial.setOnClickListener {
            val intent = Intent(activity, GuideActivity::class.java)
            startActivity(intent)
        }
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Success -> {
                    if (it.value.error == "false") {
                        viewModel.saveAuthToken(it.value.user.id)
                        requireActivity().startNewActivity(MainActivity::class.java)
                    } else {
                        requireView().findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                    }
                }
                is Resource.Failure -> {
                }
            }
        })
        binding.loginBtnKakao.setOnClickListener {
            binding.loginProgressBar.visible(true)
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "로그인 실패", error)
                    binding.loginProgressBar.visible(false)
                } else if (token != null) {
                    Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
                    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                        if (error != null) {
                            Log.e(ContentValues.TAG, "토큰 정보 보기 실패", error)
                        }
                        else if (tokenInfo != null) {
                            Log.i(ContentValues.TAG, "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초")
                            viewModel.login(tokenInfo.id)
                        }
                    }
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(GlobalApplication.getGlobalApplicationContext()))
            {
                UserApiClient.instance.loginWithKakaoTalk(GlobalApplication.getGlobalApplicationContext(), callback = callback)
            } else
            {
                UserApiClient.instance.loginWithKakaoAccount(GlobalApplication.getGlobalApplicationContext(), callback = callback)
            }
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