package com.mapo.walkaholic.ui.main.challenge

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.Mission
import com.mapo.walkaholic.data.model.MissionCondition
import com.mapo.walkaholic.data.model.MissionDaily
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.challenge.mission.ChallengeDetailMissionAdapter
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeRankingViewPagerAdapter
import com.mapo.walkaholic.ui.snackbar
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChallengeDetailFragment(
    private val position: Int
) : BaseSharedFragment<ChallengeViewModel, FragmentDetailChallengeBinding, MainRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : ChallengeViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ChallengeDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ChallengeDetailFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)

        val dummyMission1 =
            MissionCondition("00", "01", "3,000 걸음", "3000", "100")
        val dummyMission2 =
            MissionCondition("00", "02", "5,000 걸음", "5000", "200")
        val dummyMission3 =
            MissionCondition("01", "02", "일일미션 5회", "5", "500")
        val dummyMission4 =
            MissionCondition("01", "02", "일일미션 10회", "10", "1000")
        val dummyArrayList: ArrayList<MissionCondition> = ArrayList()
        dummyArrayList.add(dummyMission1)
        dummyArrayList.add(dummyMission2)

        viewModel.userResponse.observe(viewLifecycleOwner, Observer { it2 ->
            binding.challengeRVMission.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                when (it2) {
                    is Resource.Success -> {
                        if (!it2.value.error) {
                            viewModel.getMissionCondition(when (position) {0 -> "00" 1 -> "01" else -> "" })
                            viewModel.missionConditionResponse.observe(viewLifecycleOwner, Observer { it3 ->
                                binding.challengeRVMission.also {
                                    it.layoutManager = LinearLayoutManager(requireContext())
                                    //동일한 크기의 아이템 항목을 사용자에게 리스트로 보여주기 위해 크기가 변경되지 않음을 명시
                                    it.setHasFixedSize(true)
                                    when (it3) {
                                        is Resource.Success -> {
                                            if (!it2.value.error) {
                                                // @TODO GET DATA WHEN REST EXECUTE SUCCESSFUL
                                            }
                                            //Log.e("missionCondition", it3.value.missionCondition.toString())
                                        }
                                        is Resource.Loading -> {

                                        }
                                        is Resource.Failure -> {
                                            it3.errorBody?.let { it1 -> Log.e("missionCondition", it1.string()) }
                                            handleApiError(it3)
                                        }
                                    }
                                    when (position) {
                                        0 -> {
                                            binding.challengeTvIntro1.text = "일일미션을 완료하고\n포인트를 받으세요!"
                                            binding.challengeTvIntro2.text = "미션은 매일 자정에 갱신되어요"
                                            binding.challengeTvIntro1.visibility = View.VISIBLE
                                            binding.challengeTvIntro2.visibility = View.VISIBLE
                                            binding.challengeLayoutRanking.visibility = View.GONE
                                            binding.challengeLayoutRankingIntro.visibility = View.GONE
                                            binding.challengeLayoutMission.visibility = View.VISIBLE
                                            /*it.adapter = it3.value.missionCondition?.let { it3 ->
                                                ChallengeDetailMissionAdapter(dummyArrayList)
                                            }*/
                                            it.adapter = ChallengeDetailMissionAdapter(dummyArrayList)
                                        }
                                        1 -> {
                                            binding.challengeTvIntro1.text = "주간미션을 완료하고\n포인트를 받으세요!"
                                            binding.challengeTvIntro2.text = "미션은 매주 월요일 자정에 갱신되어요"
                                            binding.challengeTvIntro1.visibility = View.VISIBLE
                                            binding.challengeTvIntro2.visibility = View.VISIBLE
                                            binding.challengeLayoutRanking.visibility = View.GONE
                                            binding.challengeLayoutRankingIntro.visibility = View.GONE
                                            binding.challengeLayoutMission.visibility = View.VISIBLE
                                            /*it.adapter = it3.value.missionCondition?.let { it3 ->
                                                ChallengeDetailMissionAdapter(dummyArrayList)
                                            }*/
                                            it.adapter = ChallengeDetailMissionAdapter(dummyArrayList)
                                        }
                                        2 -> {
                                            //observe 하여 user 네임, 닉넴 뿌려주기
                                            binding.challengeTvIntro1.visibility = View.GONE
                                            binding.challengeTvIntro2.visibility = View.GONE
                                            binding.challengeLayoutMission.visibility = View.GONE
                                            binding.challengeLayoutRankingIntro.visibility = View.VISIBLE

                                            tabLayout = binding.challengeRankingTL
                                            viewPager = binding.challengeRankingVP
                                            val adapter = ChallengeRankingViewPagerAdapter(this, 2)
                                            viewPager.adapter = adapter
                                            val tabName: ArrayList<String> = arrayListOf()
                                            tabName.add("월별포인트")
                                            tabName.add("누적포인트")
                                            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                                tab.text = tabName?.get(position)
                                            }.attach()
                                            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                                                override fun onTabSelected(tab: TabLayout.Tab?) {
                                                    viewPager.currentItem = tab!!.position
                                                    when(tab!!.position) {
                                                        0 -> {
                                                            binding.challengeRankingTvIntro1.text = "${it2.value.user.user_nick_name}님, 월별랭킹"
                                                            // binding. ~~ 순위
                                                            binding.challengeRankingTvIntro3.text = "월별랭킹은 매월 1일 자정에 갱신되어요"
                                                        }
                                                        1 -> {
                                                            binding.challengeRankingTvIntro1.text = "${it2.value.user.user_nick_name}님, 누적랭킹"
                                                            // binding. ~~ 순위
                                                            binding.challengeRankingTvIntro3.text = "서비스 시작일(2021년 05월 17일)부터 현재까지"
                                                        }
                                                        else -> { }
                                                    }
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
                            })
                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        handleApiError(it2)
                    }
                }
            }
        })
        viewModel.getUser()
    }

    private fun showToastEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
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
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                requireView().snackbar(contents)
            }
        }
    }

    override fun getViewModel() = ChallengeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailChallengeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}