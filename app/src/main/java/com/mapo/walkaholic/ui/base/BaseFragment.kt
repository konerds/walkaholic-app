package com.mapo.walkaholic.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.mapo.walkaholic.data.UserPreferences
import com.mapo.walkaholic.data.network.RemoteDataSource
import com.mapo.walkaholic.data.repository.BaseRepository
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class BaseFragment<VM : BaseViewModel, B : ViewBinding, R : BaseRepository> : Fragment() {
    protected lateinit var userPreferences: UserPreferences
    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    private lateinit var callback: OnBackPressedCallback
    protected val remoteDataSource = RemoteDataSource()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userPreferences = UserPreferences(requireContext())
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        lifecycleScope.launch { userPreferences.jwtToken.first() }

        return binding.root
    }

    fun logout() = lifecycleScope.launch {
        viewModel.logout()
        userPreferences.removeJwtToken()
        requireActivity().startNewActivity(AuthActivity::class.java as Class<Activity>)
    }

    abstract fun getViewModel(): Class<VM>
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun getFragmentRepository(): R
}