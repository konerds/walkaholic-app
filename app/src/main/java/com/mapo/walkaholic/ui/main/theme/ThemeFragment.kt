package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.network.APISApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SGISApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.DashboardThemeAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeFragment : BaseFragment<ThemeViewModel, FragmentThemeBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.themeEnumResponse.observe(viewLifecycleOwner, Observer { it2 ->
            binding.themeRVTheme.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                when (it2) {
                    is Resource.Success -> {
                        if (!it2.value.error) {
                            it.adapter =
                                it2.value.themeEnum?.let { it3 -> ThemeAdapter(it3) }
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
        viewModel.getThemeEnum()
    }
    override fun getViewModel() = ThemeViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentThemeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : MainRepository {
        val accessToken = runBlocking { userPreferences.accessToken.first() }
        val api = remoteDataSource.buildRetrofitApi(InnerApi::class.java, accessToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(APISApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SGISApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}