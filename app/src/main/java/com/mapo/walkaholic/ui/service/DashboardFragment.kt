package com.mapo.walkaholic.ui.service

import android.annotation.SuppressLint
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
import androidx.lifecycle.Observer
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.User
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.visible
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dashProgressBar.visible(false)

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e(TAG, "토큰 정보 보기 실패", error)
            }
            else if (tokenInfo != null) {
                Log.i(TAG, "토큰 정보 보기 성공" +
                        "\n회원번호: ${tokenInfo.id}" +
                        "\n만료시간: ${tokenInfo.expiresIn} 초")
                viewModel.getUser(tokenInfo.id)
                viewModel.user.observe(viewLifecycleOwner, Observer {
                    when(it) {
                        is Resource.Success -> {
                            binding.dashProgressBar.visible(false)
                            Log.e(TAG, "토큰 정보 보기 성공\n${it.value.user}")
                            updateUI(it.value.user)
                        }
                        is Resource.Loading -> {
                            binding.dashProgressBar.visible(true)
                            Log.e(TAG, "토큰 정보 보기 실패\n${it}")
                        }
                    }
                })
            }
        }
    }

    private fun updateUI(user: User) {
        with(binding) {
            dashTvIntro.text = "파뿌리가 ${user.nickname}님을 기다렸어요!"
            val spannableTvWalkToday = dashTvWalkToday.text as Spannable
            spannableTvWalkToday.setSpan(
                    ForegroundColorSpan(Color.parseColor("#F97413")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : DashboardRepository {
        //val id = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(Api::class.java)
        return DashboardRepository(api)
    }
}