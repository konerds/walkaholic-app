package com.mapo.walkaholic.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.FragmentLoginBinding
import com.mapo.walkaholic.data.network.AuthApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.GuideActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.enable
import com.mapo.walkaholic.ui.main.MainActivity
import com.mapo.walkaholic.ui.startNewActivity
import com.mapo.walkaholic.ui.visible

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.loginProgressBar.visible(false)
        binding.loginBtnLogin.enable(false)
        binding.loginBtnTutorial.setOnClickListener {
            requireActivity().startNewActivity(GuideActivity::class.java)
        }
        binding.loginBtnRegister.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.loginEtUserPassword.windowToken, 0)
            binding.loginEtUserPassword.clearFocus()
            findNavController().navigate(R.id.registerFragment)
        }
        viewModel.authResponse.observe(viewLifecycleOwner, Observer {
            binding.loginProgressBar.visible(false)
            when (it) {
                is Resource.Success -> {
                    viewModel.saveAuthToken(it.value.user.access_token)
                    requireActivity().startNewActivity(MainActivity::class.java)
                    Toast.makeText(
                        requireContext(),
                        it.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "${getString(R.string.login)} ${getString(R.string.fail)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.loginEtUserId.addTextChangedListener {
            val userPassword = binding.loginEtUserPassword.text.toString().trim()
            binding.loginBtnLogin.enable(userPassword.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.loginEtUserPassword.addTextChangedListener {
            val userId = binding.loginEtUserId.text.toString().trim()
            binding.loginBtnLogin.enable(userId.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.loginBtnLogin.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userId: String = binding.loginEtUserId.text.toString().trim()
            val userPassword: String = binding.loginEtUserPassword.text.toString().trim()
            imm.hideSoftInputFromWindow(binding.loginEtUserPassword.windowToken, 0)
            binding.loginProgressBar.visible(true)
            viewModel.login(userId, userPassword)
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences)
}