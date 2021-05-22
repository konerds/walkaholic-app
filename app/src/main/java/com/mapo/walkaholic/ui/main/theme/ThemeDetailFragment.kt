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
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
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
        /*******************
         * 테스트용 임시
         */
        binding.themeRVTheme.also { _themeRVTheme ->
            _themeRVTheme.layoutManager = LinearLayoutManager(requireContext())
            _themeRVTheme.setHasFixedSize(true)
            val dummyData : ArrayList<Theme> = arrayListOf()
            dummyData.add(Theme(1, "테마1 이름", "테마1 제목", "테마1 주소", "", "10", "1.5"))
            dummyData.add(Theme(2, "테마2 이름", "테마2 제목", "테마2 주소", "", "20", "2.5"))
            dummyData.add(Theme(3, "테마3 이름", "테마3 제목", "테마3 주소", "", "30", "3.5"))
            _themeRVTheme.adapter = ThemeDetailAdapter(dummyData)
        }
        /************************/
        viewModel.themeResponse.observe(viewLifecycleOwner, Observer { _themeResponse ->
            when (_themeResponse) {
                is Resource.Success -> {
                    when (_themeResponse.value.code) {
                        "200" -> {
                            binding.themeRVTheme.also { _themeRVTheme ->
                                _themeRVTheme.layoutManager = LinearLayoutManager(requireContext())
                                _themeRVTheme.setHasFixedSize(true)
                                _themeRVTheme.adapter = ThemeDetailAdapter(_themeResponse.value.data)
                            }
                        }
                        "400" -> {
                            // Error
                        }
                        else -> {
                            // Error
                        }
                    }
                    Log.e("Theme", _themeResponse.value.data.toString())
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    _themeResponse.errorBody?.let { _errorBody -> Log.e("Theme_Detail", _errorBody.string()) }
                    handleApiError(_themeResponse)
                }
            }
        })
        viewModel.getTheme(when(position) { 0 -> "001" 1 -> "002" 2 -> "003" else -> ""})
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