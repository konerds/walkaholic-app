package com.mapo.walkaholic.ui.main.challenge

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentChallengeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.main.challenge.mission.ChallengeDetailMissionListener
import com.mapo.walkaholic.ui.main.map.MapFragmentArgs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeFragment : BaseFragment<ChallengeViewModel, FragmentChallengeBinding, MainRepository>()
{
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    val challengeArgs: ChallengeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.challengeTL
        viewPager = binding.challengeVP
        val adapter = ChallengeViewPagerAdapter(childFragmentManager, lifecycle, 3)
        viewPager.adapter = adapter
        val tabName : ArrayList<String> = arrayListOf()
        tabName.add("일일미션")
        tabName.add("주간미션")
        tabName.add("랭킹")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabName?.get(position)
        }.attach()

        Log.e("challengePosition", challengeArgs.idChallenge.toString())

        /*tabLayout.getTabAt(challengeArgs.idChallenge)?.select()
        viewPager.currentItem = challengeArgs.idChallenge*/

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        /*when(challengeArgs.idChallenge) {
            0 -> {
                val currentTab = tabLayout.getTabAt(0)
                if(currentTab != null) {
                    currentTab.select()
                    viewPager.currentItem = 0
                }
            }
            1 -> {
                val currentTab = tabLayout.getTabAt(1)
                if(currentTab != null) {
                    currentTab.select()
                    viewPager.currentItem = 1
                }
            }
            2 -> {
                val currentTab = tabLayout.getTabAt(2)
                if(currentTab != null) {
                    currentTab.select()
                    viewPager.currentItem = 2
                }
            }
            else -> {
                // Never Occur
            }
        }*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmDialog(getString(com.mapo.walkaholic.R.string.err_deny_prev), null, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun getViewModel() = ChallengeViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentChallengeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}