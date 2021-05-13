package com.mapo.walkaholic.ui.main.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeBinding
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeDetailFragment(
    private val position: Int
) : BaseFragment<ChallengeDetailViewModel, FragmentDetailChallengeBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(position) {
            in 0..1 -> {
                val dummyMission1 = Theme(null,null,"mission1",null,null,null,null,null,null,null,null)
                val dummyMission2 = Theme(null,null,"mission2",null,null,null,null,null,null,null,null)
                val dummyArrayList : ArrayList<Theme> = ArrayList()
                dummyArrayList.add(dummyMission1)
                dummyArrayList.add(dummyMission2)
                binding.challengeLayoutRanking.visibility = View.GONE
                binding.challengeLayoutMission.visibility = View.VISIBLE
                binding.challengeRVMission.layoutManager = LinearLayoutManager(requireContext())
                binding.challengeRVMission.setHasFixedSize(true)
                binding.challengeRVMission.adapter = ChallengeDetailMissionAdapter(dummyArrayList)
            }
            2 -> {
                val dummyRanking1 = Theme(null,null,"ranking1",null,null,null,null,null,null,null,null)
                val dummyRanking2 = Theme(null,null,"ranking2",null,null,null,null,null,null,null,null)
                val dummyArrayList : ArrayList<Theme> = ArrayList()
                dummyArrayList.add(dummyRanking1)
                dummyArrayList.add(dummyRanking2)
                binding.challengeLayoutMission.visibility = View.GONE
                binding.challengeLayoutRanking.visibility = View.VISIBLE
                binding.challengeRVRanking.layoutManager = LinearLayoutManager(requireContext())
                binding.challengeRVRanking.setHasFixedSize(true)
                binding.challengeRVRanking.adapter = ChallengeDetailRankingAdapter(dummyArrayList)
            }
        }
    }

    override fun getViewModel() = ChallengeDetailViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailChallengeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}