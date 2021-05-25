package com.mapo.walkaholic.ui.main.challenge.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.Ranking
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeRankingBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.theme.ThemeDetailAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeDetailRankingFragment(
    private val position: Int
) : BaseFragment<ChallengeDetailRankingViewModel, FragmentDetailChallengeRankingBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyMission1 =
            Ranking("00", "01", "1", "유니", "5,000")
        val dummyMission2 =
            Ranking("01", "01", "2", "미나리", "3,000")
        val dummyMission3 =
            Ranking("02", "03", "3", "고구마", "1,000")
        val dummyMission4 =
            Ranking("03", "04", "4", "오렌지", "800")
        val dummyMission5 =
            Ranking("04", "03", "5", "딸기", "500")
        val dummyMission6 =
            Ranking("05", "04", "6", "수박", "300")
        val dummyMission7 =
            Ranking("06", "03", "7", "메론", "500")
        val dummyMission8 =
            Ranking("07", "04", "8", "포도", "300")
        val dummyArrayList: ArrayList<Ranking> = ArrayList()
        dummyArrayList.add(dummyMission1)
        dummyArrayList.add(dummyMission2)
        dummyArrayList.add(dummyMission3)
        dummyArrayList.add(dummyMission4)
        dummyArrayList.add(dummyMission5)
        dummyArrayList.add(dummyMission6)
        dummyArrayList.add(dummyMission7)
        dummyArrayList.add(dummyMission8)

        viewModel.monthRankingResponse.observe(viewLifecycleOwner, Observer { _monthRankingResponse ->
            binding.challengeRVRanking.also { _challengeRVRanking ->
                when (_monthRankingResponse) {
                    is Resource.Success -> {
                        when (_monthRankingResponse.value.code) {
                            "200" -> {
                                binding.challengeRVRanking.also { _monthRankingRV ->
                                    _monthRankingRV.layoutManager = LinearLayoutManager(requireContext())
                                    _monthRankingRV.setHasFixedSize(true)
                                    _monthRankingRV.adapter =
                                        ChallengeDetailRankingAdapter(_monthRankingResponse.value.data)
                                }
                            }
                            else -> {
                                confirmDialog(
                                    _monthRankingResponse.value.message,
                                    {
                                        viewModel.getMonthRanking()
                                    },
                                    "재시도"
                                )
                            }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        // Loading
                    }
                    is Resource.Failure -> {
                        // Network Error
                        handleApiError(_monthRankingResponse) { viewModel.getMonthRanking() }
                    }
                }
            }
        })
        viewModel.getRanking(position)

        val layoutManger = LinearLayoutManager(requireContext())
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        binding.challengeRVRanking.layoutManager = layoutManger
        binding.challengeRVRanking.setHasFixedSize(true)

        when (position) {
            0 -> {
                binding.challengeRVRanking.adapter = ChallengeDetailRankingAdapter(dummyArrayList)
            }
            1 -> {
                binding.challengeRVRanking.adapter = ChallengeDetailRankingAdapter(dummyArrayList)
            }
        }
        binding.challengeRankingList.visibility = View.VISIBLE
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