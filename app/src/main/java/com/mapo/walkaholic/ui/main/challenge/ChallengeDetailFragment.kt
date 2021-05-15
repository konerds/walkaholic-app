package com.mapo.walkaholic.ui.main.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.main.challenge.mission.ChallengeDetailMissionAdapter
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeRankingViewPagerAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeDetailFragment(
    private val position: Int
) : BaseFragment<ChallengeDetailViewModel, FragmentDetailChallengeBinding, MainRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (position) {
            0 -> {
                val dummyMission1 =
                    Theme(null, null, "day_mission1", null, null, null, null, null, null, null, null)
                val dummyMission2 =
                    Theme(null, null, "day_mission2", null, null, null, null, null, null, null, null)
                val dummyArrayList: ArrayList<Theme> = ArrayList()
                dummyArrayList.add(dummyMission1)
                dummyArrayList.add(dummyMission2)
                binding.challengeTvIntro1.text = "일일미션을 완료하고\n포인트를 받으세요!"
                binding.challengeTvIntro2.text = "미션은 매일 자정에 갱신되어요"
                binding.challengeTvIntro1.visibility = View.VISIBLE
                binding.challengeTvIntro2.visibility = View.VISIBLE
                binding.challengeLayoutRanking.visibility = View.GONE
                binding.challengeLayoutRankingIntro.visibility = View.GONE
                binding.challengeLayoutMission.visibility = View.VISIBLE
                binding.challengeRVMission.layoutManager = LinearLayoutManager(requireContext())
                binding.challengeRVMission.setHasFixedSize(true)
                binding.challengeRVMission.adapter = ChallengeDetailMissionAdapter(dummyArrayList)
            }
            1 -> {
                val dummyMission1 =
                    Theme(null, null, "week_mission1", null, null, null, null, null, null, null, null)
                val dummyMission2 =
                    Theme(null, null, "week_mission2", null, null, null, null, null, null, null, null)
                val dummyArrayList: ArrayList<Theme> = ArrayList()
                dummyArrayList.add(dummyMission1)
                dummyArrayList.add(dummyMission2)
                binding.challengeTvIntro1.text = "주간미션을 완료하고\n포인트를 받으세요!"
                binding.challengeTvIntro2.text = "미션은 매주 월요일 자정에 갱신되어요"
                binding.challengeTvIntro1.visibility = View.VISIBLE
                binding.challengeTvIntro2.visibility = View.VISIBLE
                binding.challengeLayoutRanking.visibility = View.GONE
                binding.challengeLayoutRankingIntro.visibility = View.GONE
                binding.challengeLayoutMission.visibility = View.VISIBLE
                binding.challengeRVMission.layoutManager = LinearLayoutManager(requireContext())
                binding.challengeRVMission.setHasFixedSize(true)
                binding.challengeRVMission.adapter = ChallengeDetailMissionAdapter(dummyArrayList)
            }
            2 -> {
                binding.challengeTvIntro1.visibility = View.GONE
                binding.challengeTvIntro2.visibility = View.GONE
                binding.challengeLayoutMission.visibility = View.GONE
                binding.challengeLayoutRankingIntro.visibility = View.VISIBLE
                tabLayout = binding.challengeRankingTL
                viewPager = binding.challengeRankingVP
                val adapter = ChallengeRankingViewPagerAdapter(this, 2)
                viewPager.adapter = adapter
                val tabName : ArrayList<String> = arrayListOf()
                tabName.add("월별포인트")
                tabName.add("누적포인트")
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = tabName?.get(position)
                }.attach()
                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        viewPager.currentItem = tab!!.position
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                })
                binding.challengeLayoutRanking.visibility = View.VISIBLE
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