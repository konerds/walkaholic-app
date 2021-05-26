package com.mapo.walkaholic.ui.favoritepath

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentFavoritePathBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FavoritePathFragment :
    BaseFragment<FavoritePathViewModel, FragmentFavoritePathBinding, MainRepository>(),
    FavoritePathClickListener {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var checkedFavoritePathMap = mutableMapOf<Int, Pair<Boolean, Theme>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.favoritePathTL
        viewPager = binding.favoritePathVP

        val adapter = FavoritePathViewPagerAdapter(childFragmentManager, lifecycle, 2, this)
        viewPager.adapter = adapter
        val tabName: ArrayList<String> = arrayListOf()
        tabName.add("테마")
        tabName.add("코스추천")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabName?.get(position)
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                onItemClick(checkedFavoritePathMap)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
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

    override fun getViewModel() = FavoritePathViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFavoritePathBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onItemClick(_checkedFavoritePathMap: MutableMap<Int, Pair<Boolean, Theme>>) {
        checkedFavoritePathMap = _checkedFavoritePathMap
        if (checkedFavoritePathMap.filter { _filterCheckedFavoritePathMap ->
                _filterCheckedFavoritePathMap.value.first
            }.isNullOrEmpty()) {
            binding.favoritePathLayoutDelete.visibility = View.GONE
        } else {
            binding.favoritePathLayoutDelete.visibility = View.VISIBLE
        }
    }
}