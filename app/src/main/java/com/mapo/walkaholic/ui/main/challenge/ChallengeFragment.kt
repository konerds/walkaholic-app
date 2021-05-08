package com.mapo.walkaholic.ui.main.challenge

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentChallengeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChallengeFragment : BaseFragment<ChallengeViewModel, FragmentChallengeBinding, MainRepository>() {
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
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}