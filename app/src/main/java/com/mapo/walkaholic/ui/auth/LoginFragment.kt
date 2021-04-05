package com.mapo.walkaholic.ui.auth

import android.content.ContentValues
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.service.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import com.mapo.walkaholic.ui.visible

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {
    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(ContentValues.TAG, "로그인 실패", error)
        } else if (token != null) {
            Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.loginProgressBar.visible(false)
        binding.loginBtnTutorial.setOnClickListener {
            requireActivity().startNewActivity(GuideActivity::class.java)
        }

        binding.loginBtnKakao.setOnClickListener {
            binding.loginProgressBar.visible(true)
            //viewModel.login()
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(GlobalApplication.getGlobalApplicationContext()))
            {
                UserApiClient.instance.loginWithKakaoTalk(GlobalApplication.getGlobalApplicationContext(), callback = callback)
            } else
            {
                UserApiClient.instance.loginWithKakaoAccount(GlobalApplication.getGlobalApplicationContext(), callback = callback)
            }
            requireActivity().startNewActivity(MainActivity::class.java)
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