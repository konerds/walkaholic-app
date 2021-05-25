package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeDetailFragment(
    private val position: Int
) : BaseFragment<ThemeDetailViewModel, FragmentDetailThemeBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTheme(position)
        viewModel.themeResponse.observe(viewLifecycleOwner, Observer { _themeResponse ->
            when (_themeResponse) {
                is Resource.Success -> {
                    when (_themeResponse.value.code) {
                        "200" -> {
                            binding.themeRVTheme.also { _themeRVTheme ->
                                _themeRVTheme.layoutManager = LinearLayoutManager(requireContext())
                                _themeRVTheme.setHasFixedSize(true)
                                _themeRVTheme.adapter =
                                    ThemeDetailAdapter(_themeResponse.value.data)
                            }
                        }
                        "400" -> {
                            // Error
                            handleApiError(_themeResponse as Resource.Failure) { viewModel.getTheme(position) }
                        }
                        else -> {
                            // Error
                            handleApiError(_themeResponse as Resource.Failure) { viewModel.getTheme(position) }
                        }
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_themeResponse) { viewModel.getTheme(position) }
                }
            }
        })
    }

    override fun getViewModel() = ThemeDetailViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailThemeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}