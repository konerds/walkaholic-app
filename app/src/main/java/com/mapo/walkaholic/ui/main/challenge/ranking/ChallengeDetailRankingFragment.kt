package com.mapo.walkaholic.ui.main.challenge.ranking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.MissionCondition
import com.mapo.walkaholic.data.model.Ranking
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeRankingBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.challenge.ChallengeViewModel
import com.mapo.walkaholic.ui.main.challenge.mission.ChallengeDetailMissionAdapter
import com.mapo.walkaholic.ui.main.theme.ThemeDetailAdapter
import com.mapo.walkaholic.ui.main.theme.ThemeViewModel
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeDetailRankingFragment(
    private val position: Int
) : BaseSharedFragment<ChallengeViewModel, FragmentDetailChallengeRankingBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel: ChallengeViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ChallengeDetailRankingFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ChallengeDetailRankingFragment::showSnackbarEvent)
        )
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

        viewModel.rankingResponse.observe(viewLifecycleOwner, Observer { it2 ->
            binding.challengeRVRanking.also {
                val layoutManger = LinearLayoutManager(requireContext())
                layoutManger.orientation = LinearLayoutManager.VERTICAL
                it.layoutManager = layoutManger
                it.setHasFixedSize(true)
                when (it2) {
                    is Resource.Success -> {
                        if (!it2.value.error) {
                            it.adapter =
                                it2.value.ranking?.let { it3 -> ChallengeDetailRankingAdapter(it3) }
                        }
                        //Log.e("Ranking", it2.value.ranking.toString())
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        //it2.errorBody?.let { it1 -> Log.e("Ranking", it1.string()) }
                        handleApiError(it2)
                    }
                }
            }
        })
        viewModel.getRanking(
            when (position) {
                0 -> "00"
                1 -> "01"
                else -> ""
            }
        )

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

    private fun showToastEvent(contents: String) {
        when (contents) {
            null -> {
            }
            "" -> {
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    contents,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSnackbarEvent(contents: String) {
        when (contents) {
            null -> {
            }
            "" -> {
            }
            else -> {
                requireView().snackbar(contents)
            }
        }
    }

    override fun getViewModel() = ChallengeViewModel::class.java

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