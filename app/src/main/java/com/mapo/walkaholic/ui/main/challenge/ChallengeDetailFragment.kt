package com.mapo.walkaholic.ui.main.challenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.response.MissionResponse
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailChallengeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.challenge.mission.ChallengeDetailMissionAdapter
import com.mapo.walkaholic.ui.main.challenge.mission.ChallengeDetailMissionListener
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeRankingViewPagerAdapter
import com.mapo.walkaholic.ui.main.dashboard.DashboardFragmentDirections
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeDetailFragment(
    private val position: Int
    ) : BaseFragment<ChallengeDetailViewModel, FragmentDetailChallengeBinding, MainRepository>(), ChallengeDetailMissionListener {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("position", position.toString())

        viewModel.getUser()
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            binding.challengeRVMission.also { _challengeRVMission ->
                _challengeRVMission.layoutManager = LinearLayoutManager(requireContext())
                _challengeRVMission.setHasFixedSize(true)
                when (_userResponse) {
                    is Resource.Success -> {
                        when (_userResponse.value.code) {
                            "200" -> {
                                viewModel.getMission(_userResponse.value.data.first().id, position)
                                viewModel.missionResponse.observe(viewLifecycleOwner, Observer { _missionResponse ->
                                    when (_missionResponse) {
                                        is Resource.Success -> {
                                            when (_missionResponse.value.code) {
                                                "200" -> {
                                                    Log.e("missionResponse", _missionResponse.value.data.toString())

                                                    _challengeRVMission.adapter =
                                                        ChallengeDetailMissionAdapter(_missionResponse.value.data, this)

                                                    val filteredData = _missionResponse.value.data
                                                    filteredData.add(MissionResponse.Mission("7", "모든미션완료", "0", "0", "모든 "))
                                                    _challengeRVMission.adapter =
                                                        ChallengeDetailMissionAdapter(filteredData, this)
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _missionResponse.value.message,
                                                        {
                                                            viewModel.getMission(
                                                                _userResponse.value.data.first().id,
                                                                position
                                                            )
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
                                            handleApiError(_missionResponse) {
                                                viewModel.getMission(
                                                    _userResponse.value.data.first().id,
                                                    position
                                                )
                                            }
                                        }
                                    }
                                })
                                when (position) {
                                    0 -> {
                                        binding.challengeTvIntro1.text =
                                            "${_userResponse.value.data.first().nickName}님, 현재"
                                        binding.challengeTvAchieve1.text =
                                            "${_userResponse.value.data.first().walkCount}"
                                        binding.challengeTvAchieve2.text = " 걸음 걸었어요!"
                                        binding.challengeTvIntro2.text = "미션은 매일 자정에 갱신되어요"
                                        binding.challengeTvIntro3.text = "일일미션을 완료하고 포인트를 받으세요!"
                                        binding.challengeMissionIntro.visibility = View.VISIBLE
                                        binding.challengeLayoutMission.visibility = View.VISIBLE
                                        binding.challengeLayoutRankingIntro.visibility = View.GONE
                                        binding.challengeLayoutRanking.visibility = View.GONE
                                        /*it.adapter = it3.value.missionCondition?.let { it3 ->
                                            ChallengeDetailMissionAdapter(dummyArrayList)
                                        }*/
                                    }
                                    1 -> {
                                        binding.challengeTvIntro1.text =
                                            "${_userResponse.value.data.first().nickName}님, 현재 일일미션"
                                        binding.challengeTvAchieve1.text = "${_userResponse.value.data.first().weeklyMissionAchievement.toString()}회"
                                        binding.challengeTvAchieve2.text = "를 달성 했어요!"
                                        binding.challengeTvIntro2.text = "미션은 매일 자정에 갱신되어요"
                                        binding.challengeTvIntro3.text = "주간미션을 완료하고 포인트를 받으세요!"
                                        binding.challengeMissionIntro.visibility = View.VISIBLE
                                        binding.challengeLayoutMission.visibility = View.VISIBLE
                                        binding.challengeLayoutRankingIntro.visibility = View.GONE
                                        binding.challengeLayoutRanking.visibility = View.GONE
                                        /*it.adapter = it3.value.missionCondition?.let { it3 ->
                                            ChallengeDetailMissionAdapter(dummyArrayList)
                                        }*/
                                    }
                                    2 -> {
                                        binding.challengeMissionIntro.visibility = View.GONE
                                        binding.challengeLayoutMission.visibility = View.GONE
                                        binding.challengeLayoutRankingIntro.visibility = View.VISIBLE
                                        binding.challengeLayoutRanking.visibility = View.VISIBLE
                                        tabLayout = binding.challengeRankingTL
                                        viewPager = binding.challengeRankingVP
                                        val adapter =
                                            ChallengeRankingViewPagerAdapter(
                                                childFragmentManager,
                                                lifecycle,
                                                2
                                            )
                                        viewPager.adapter = adapter
                                        val tabName: ArrayList<String> = arrayListOf()
                                        tabName.add("월별포인트")
                                        tabName.add("누적포인트")
                                        TabLayoutMediator(
                                            tabLayout,
                                            viewPager
                                        ) { tab, position ->
                                            tab.text = tabName?.get(position)
                                        }.attach()

                                        viewModel.getMonthRanking(_userResponse.value.data.first().id)
                                        viewModel.monthRankingResponse.observe(viewLifecycleOwner, Observer { _monthRankingResponse ->
                                            when (_monthRankingResponse) {
                                                is Resource.Success -> {
                                                    when (_monthRankingResponse.value.code) {
                                                        "200" -> {
                                                            binding.challengeRankingTvIntro1.text =
                                                                "${_userResponse.value.data.first().nickName}님, 월별랭킹"
                                                            binding.challengeRankingTvRankNum.text = "${_monthRankingResponse.value.data.first().rank}"
                                                            binding.challengeRankingTvIntro3.text =
                                                                "월별랭킹은 매월 1일 자정에 갱신되어요"
                                                        }
                                                        else -> {
                                                            // Error
                                                            confirmDialog(
                                                                _monthRankingResponse.value.message,
                                                                {
                                                                    viewModel.getMonthRanking(
                                                                        _userResponse.value.data.first().id
                                                                    )
                                                                },
                                                                "재시도")
                                                        }
                                                    }
                                                }
                                                is Resource.Loading -> {
                                                    // Loading
                                                }
                                                is Resource.Failure -> {
                                                    // Network Error
                                                    handleApiError(_monthRankingResponse) {
                                                        viewModel.getMonthRanking(
                                                            _userResponse.value.data.first().id)
                                                    }
                                                }
                                            }
                                        })

                                        viewModel.accumulateRankingResponse.observe(viewLifecycleOwner, Observer { _accumulateRankingResponse ->
                                            when (_accumulateRankingResponse) {
                                                is Resource.Success -> {
                                                    when (_accumulateRankingResponse.value.code) {
                                                        "200" -> {
                                                            binding.challengeRankingTvIntro1.text =
                                                                "${_userResponse.value.data.first().nickName}님, 누적랭킹"
                                                            //레벨
                                                            binding.challengeRankingTvRankNum.text = "${_accumulateRankingResponse.value.data.first().rank}"
                                                            binding.challengeRankingTvIntro3.text =
                                                                "서비스 시작일(2021년 05월 17일)부터 현재까지"
                                                        }
                                                        else -> {
                                                            // Error
                                                            confirmDialog(
                                                                _accumulateRankingResponse.value.message,
                                                                {
                                                                    viewModel.getAccumulateRanking(
                                                                        _userResponse.value.data.first().id
                                                                    )
                                                                },
                                                                "재시도")
                                                        }
                                                    }
                                                }
                                                is Resource.Loading -> {
                                                    // Loading
                                                }
                                                is Resource.Failure -> {
                                                    // Network Error
                                                    handleApiError(_accumulateRankingResponse) {
                                                        viewModel.getAccumulateRanking(
                                                            _userResponse.value.data.first().id)
                                                    }
                                                }
                                            }
                                        })

                                        tabLayout.addOnTabSelectedListener(object :
                                            TabLayout.OnTabSelectedListener {
                                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                                viewPager.currentItem = tab!!.position
                                                when (tab!!.position) {
                                                    0 -> {
                                                        viewModel.getMonthRanking(_userResponse.value.data.first().id)
                                                    }
                                                    1 -> {
                                                        viewModel.getAccumulateRanking(_userResponse.value.data.first().id)
                                                    }
                                                }
                                            }
                                            override fun onTabUnselected(tab: TabLayout.Tab?) {

                                            }

                                            override fun onTabReselected(tab: TabLayout.Tab?) {

                                            }
                                        })
                                    }
                                }
                            }
                            else -> {
                                // Error
                                confirmDialog(
                                    _userResponse.value.message,
                                    {
                                        viewModel.getUser()
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
                        handleApiError(_userResponse) { viewModel.getUser() }
                    }
                }
            }
        })
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
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onItemClick(view: View, pos: Int) {
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
                when (_userResponse) {
                    is Resource.Success -> {
                        when (_userResponse.value.code) {
                            "200" -> {
                                var missionId: Int = 0
                                val missionArr = arrayOf(arrayOf(1, 2, 3), arrayOf(4, 5, 6))
                                when(position) {
                                    0 -> {
                                        missionId = missionArr[0][pos]
                                    }
                                    1 -> {
                                        missionId = missionArr[1][pos]
                                    }
                                }
                                //보상 받기
                                viewModel.getMissionReward(
                                    _userResponse.value.data.first().id,
                                    missionId.toString()
                                )
                                viewModel.missionRewardResponse.observe(viewLifecycleOwner, Observer { _missionRewardResponse ->
                                    when(_missionRewardResponse) {
                                        is Resource.Success -> {
                                            when(_missionRewardResponse.value.code){
                                                "200" -> {
                                                    confirmDialog(
                                                        _missionRewardResponse.value.message,
                                                        {
                                                            val navDirection: NavDirections? =
                                                                ChallengeFragmentDirections.actionActionBnvChallengeSelf(position)
                                                            if (navDirection != null) {
                                                                findNavController().navigate(navDirection)
                                                            }
                                                        },
                                                        "확인"
                                                    )
                                                }
                                                else -> {
                                                    confirmDialog(
                                                        _missionRewardResponse.value.message,
                                                        {
                                                            viewModel.getMissionReward(
                                                                _userResponse.value.data.first().id,
                                                                missionId.toString()
                                                            )
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
                                            handleApiError(_missionRewardResponse) { viewModel.getMissionReward(
                                                _userResponse.value.data.first().id,
                                                missionId.toString()
                                            ) }
                                        }
                                    }
                                })
                            }
                            else -> {
                                // Error
                                confirmDialog(
                                    _userResponse.value.message,
                                    {
                                        viewModel.getUser()
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
                        handleApiError(_userResponse) { viewModel.getUser() }
                    }
                }
        })
    }
}