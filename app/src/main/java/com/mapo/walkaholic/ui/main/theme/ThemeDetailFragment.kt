package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoFragmentDirections
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeDetailFragment(
    private val position: Int
) : BaseFragment<ThemeDetailViewModel, FragmentDetailThemeBinding, MainRepository>(), ThemeItemClickListener {
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
                                    ThemeDetailAdapter(_themeResponse.value.data, this)
                            }
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _themeResponse.value.message,
                                {
                                    viewModel.getTheme(position)
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

    override fun onItemClick(position: Int, themeItemInfo: Theme) {
        val navDirection: NavDirections? =
            ThemeFragmentDirections.actionActionBnvThemeToActionBnvMap(themeItemInfo.courseId)
        if (navDirection != null) {
            findNavController().navigate(navDirection)
        }
    }
}