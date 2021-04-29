package com.mapo.walkaholic.ui.main.theme

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapo.walkaholic.data.network.APISApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SGISApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeFragment : BaseFragment<ThemeViewModel, FragmentThemeBinding, MainRepository>() {
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