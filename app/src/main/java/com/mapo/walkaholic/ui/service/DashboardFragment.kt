package com.mapo.walkaholic.ui.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.User
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.startNewActivity
import com.mapo.walkaholic.ui.visible
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userResponse.observe(viewLifecycleOwner, Observer {
            binding.dashProgressBar.visible(true)
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        updateUI(it.value.user)
                    } else {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().startNewActivity(AuthActivity::class.java)
                    }
                }
                is Resource.Loading -> { }
                is Resource.Failure -> {
                    Toast.makeText(
                            requireContext(),
                            getString(R.string.err_user),
                            Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        binding.dashProgressBar.visible(false)
        viewModel.getUser()
    }

    private fun updateUI(user: User) {
        with(binding) {
            dashTvIntro.text = "파뿌리가 ${user.nickname}님을 기다렸어요!"
            val spannableTvWalkToday = dashTvWalkToday.text as Spannable
            spannableTvWalkToday.setSpan(
                    ForegroundColorSpan(Color.parseColor("#F97413")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.dashProgressBar.visible(false)
    }

    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): DashboardRepository {
        //val id = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(Api::class.java)
        return DashboardRepository(api)
    }
}