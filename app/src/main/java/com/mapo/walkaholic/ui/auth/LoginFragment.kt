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
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.service.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {
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
                        viewModel.saveAuthToken()
                        requireActivity().startNewActivity(MainActivity::class.java)
                    } else {
                        Toast.makeText(
                                requireContext(),
                                it.value.message.trim(),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Failure -> {
                    val errJson = JSONObject(it.errorBody?.string())
                    Toast.makeText(
                            requireContext(),
                            errJson.getString("message"),
                            Toast.LENGTH_SHORT
                    ).show()
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
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            AuthRepository.getInstance(remoteDataSource.buildRetrofitApi(Api::class.java), userPreferences)
}