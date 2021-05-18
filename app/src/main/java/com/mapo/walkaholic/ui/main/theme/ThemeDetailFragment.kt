package com.mapo.walkaholic.ui.main.theme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ThemeDetailFragment(
    private val position: Int
) : BaseSharedFragment<ThemeViewModel, FragmentDetailThemeBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : ThemeViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ThemeDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@ThemeDetailFragment::showSnackbarEvent)
        )
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
    ) = FragmentDetailThemeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}