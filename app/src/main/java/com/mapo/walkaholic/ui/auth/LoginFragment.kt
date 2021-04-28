package com.mapo.walkaholic.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.let { binding.viewModel = it }
        viewModel.onClickEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@LoginFragment::moveActivity)
        )
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        lifecycleScope.launch {
                            viewModel.saveAuthToken()
                            requireActivity().startNewActivity(MainActivity::class.java)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            it.value.message.trim(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it) { viewModel.login() }
                    requireView().findNavController()
                        .navigate(R.id.action_loginFragment_to_registerFragment)
                }
            }
        })
        binding.loginBtnKakao.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Toast.makeText(
                        GlobalApplication.getGlobalApplicationContext(),
                        GlobalApplication.getGlobalApplicationContext()
                            .getString(R.string.err_auth),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (token != null) {
                    Toast.makeText(
                        GlobalApplication.getGlobalApplicationContext(),
                        GlobalApplication.getGlobalApplicationContext()
                            .getString(R.string.msg_success_auth),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.login()
                }
            }
            viewModel.getAuth(callback)
        }
    }

    private fun moveActivity(name: String) {
        when (name) {
            "loginToGuide" -> {
                val intent = Intent(activity, GuideActivity::class.java)
                startActivity(intent)
            }
            else -> null
        }
    }

    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository {
        val accessToken = runBlocking { userPreferences.accessToken.first() }
        return AuthRepository.getInstance(
            remoteDataSource.buildRetrofitApi(InnerApi::class.java, accessToken),
            userPreferences
        )
    }
}