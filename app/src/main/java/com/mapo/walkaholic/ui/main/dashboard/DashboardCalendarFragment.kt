package com.mapo.walkaholic.ui.main.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class DashboardCalendarFragment :
        BaseFragment<DashboardViewModel, FragmentDashboardBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }

    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val accessToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, accessToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}