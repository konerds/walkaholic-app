package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.ThemeEnum
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeFragment : BaseFragment<ThemeViewModel, FragmentThemeBinding, MainRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    companion object {
        fun newInstance() : ThemeFragment = ThemeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.themeTL
        viewPager = binding.themeVP
        viewModel.themeEnumResponse.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (!it.value.error) {
                            val adapter = it.value.themeEnum?.let { it2 -> ThemeViewPagerAdapter(this, it2.size) }
                            viewPager.adapter = adapter
                            val tabName : ArrayList<ThemeEnum>? = it.value.themeEnum
                            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                tab.text = tabName?.get(position)?.code_value
                            }.attach()
                            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                                override fun onTabSelected(tab: TabLayout.Tab?) {
                                    viewPager.currentItem = tab!!.position
                                    Log.e("VP", tab!!.position.toString())
                                }

                                override fun onTabUnselected(tab: TabLayout.Tab?) {

                                }

                                override fun onTabReselected(tab: TabLayout.Tab?) {

                                }
                            })
                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        handleApiError(it)
                    }
                }
        })
        viewModel.getThemeEnum()
    }

    override fun getViewModel() = ThemeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentThemeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}