package com.mapo.walkaholic.ui.main.challenge.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeRankingBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeDetailRankingFragment(
    private val position: Int
) : BaseFragment<ChallengeDetailRankingViewModel, FragmentDetailChallengeRankingBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.challengeRankingList.visibility = View.VISIBLE
        viewModel.getRanking(position)
        viewModel.rankingResponse.observe(viewLifecycleOwner, Observer { _rankingResponse ->
            when (_rankingResponse) {
                is Resource.Success -> {
                    when (_rankingResponse.value.code) {
                        "200" -> {
                            binding.challengeRVRanking.also { _rankingRV ->
                                _rankingRV.layoutManager = LinearLayoutManager(requireContext())
                                _rankingRV.setHasFixedSize(true)
                                _rankingRV.adapter =
                                    ChallengeDetailRankingAdapter(_rankingResponse.value.data)
                            }
                        }
                        else -> {
                            confirmDialog(
                                _rankingResponse.value.message,
                                {
                                    viewModel.getRanking(position)
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
                    handleApiError(_rankingResponse) { viewModel.getRanking(position) }
                }
            }
        })
    }

    override fun getViewModel() = ChallengeDetailRankingViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailChallengeRankingBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}