package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeFragment : BaseFragment<ThemeViewModel, FragmentThemeBinding, MainRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : ThemeViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ThemeFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ThemeFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.themeTL
        viewPager = binding.themeVP

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
                        "400" -> {
                            // Error
                        }
                        else -> {
                            // Error
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
        viewModel.getCategoryTheme()

        /*viewModel.themeEnumResponse.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (!it.value.error) {

                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        handleApiError(it)
                    }
                }
        })*/
        /*viewModel.getThemeEnum()*/
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