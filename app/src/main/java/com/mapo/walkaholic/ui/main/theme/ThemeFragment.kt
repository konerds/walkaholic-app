package com.mapo.walkaholic.ui.main.theme

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.response.CategoryThemeResponse
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.map.MapFragmentDirections
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeFragment : BaseFragment<ThemeViewModel, FragmentThemeBinding, MainRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    val themeDetailArgs: ThemeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.themeTL
        viewPager = binding.themeVP
        viewModel.getCategoryTheme()
        viewModel.categoryThemeResponse.observe(viewLifecycleOwner, Observer { _categoryThemeResponse ->
            when(_categoryThemeResponse) {
                is Resource.Success -> {
                    when(_categoryThemeResponse.value.code) {
                        "200" -> {
                            val adapter = ThemeViewPagerAdapter(childFragmentManager, lifecycle, 3)
                            viewPager.adapter = adapter
                            val tabName : ArrayList<CategoryThemeResponse.CategoryTheme>? = _categoryThemeResponse.value.data
                            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                tab.text = tabName?.get(position)?.themeName
                            }.attach()

                            // 대쉬보드에서 테마 선택시
                            tabLayout.getTabAt(themeDetailArgs.themePosition)?.select()
                            viewPager.currentItem = themeDetailArgs.themePosition
                            
                            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                                override fun onTabSelected(tab: TabLayout.Tab?) {
                                    viewPager.currentItem = tab!!.position
                                }

                                override fun onTabUnselected(tab: TabLayout.Tab?) {

                                }

                                override fun onTabReselected(tab: TabLayout.Tab?) {

                                }
                            })
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _categoryThemeResponse.value.message,
                                {
                                    viewModel.getCategoryTheme()
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
                    handleApiError(_categoryThemeResponse) { viewModel.getCategoryTheme() }
                }
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
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}