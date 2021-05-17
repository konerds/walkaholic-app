package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.util.Log
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
        viewModel.themeResponse.observe(viewLifecycleOwner, Observer { it2 ->
            binding.themeRVTheme.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                when (it2) {
                    is Resource.Success -> {
                        if (!it2.value.error) {
                            it.adapter =
                                it2.value.theme?.let { it3 -> ThemeDetailAdapter(it3) }
                        }
                        Log.e("Theme_Detail", it2.value.theme.toString())
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        it2.errorBody?.let { it1 -> Log.e("Theme_Detail", it1.string()) }
                        handleApiError(it2)
                    }
                }
            }
        })
        viewModel.getThemeDetail(when(position) { 0 -> "00" 1 -> "01" 2 -> "02" else -> ""})
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