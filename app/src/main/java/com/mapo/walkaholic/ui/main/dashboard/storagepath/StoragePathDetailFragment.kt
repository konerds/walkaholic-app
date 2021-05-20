package com.mapo.walkaholic.ui.main.dashboard.storagepath

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
import com.mapo.walkaholic.databinding.FragmentDetailStoragePathBinding
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.theme.ThemeDetailAdapter
import com.mapo.walkaholic.ui.main.theme.ThemeViewModel
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoragePathDetailFragment(
    private val position: Int
) : BaseSharedFragment<StoragePathViewModel, FragmentDetailStoragePathBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel: StoragePathViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@StoragePathDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@StoragePathDetailFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)

        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    viewModel.getStoragePath(_userResponse.value.data.first().id,)
                    viewModel.storagePathResponse.observe(viewLifecycleOwner, Observer { it2 ->
                        binding.storagePathRV.also {
                            it.layoutManager = LinearLayoutManager(requireContext())
                            it.setHasFixedSize(true)
                            when (it2) {
                                is Resource.Success -> {
                                    if (!it2.value.error) {
                                        it.adapter =
                                            it2.value.StoragePath?.let { it3 ->
                                                StoragePathDetailAdapter(
                                                    it3
                                                )
                                            }
                                    }
                                    Log.e("Storage_Path", it2.value.StoragePath.toString())
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    it2.errorBody?.let { it1 ->
                                        Log.e(
                                            "Storage_Path",
                                            it1.string()
                                        )
                                    }
                                    handleApiError(it2)
                                }
                            }
                        }
                    })
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(_userResponse)
                }
            }
        })
        //viewModel.getStoragePath(when(position) { 0 -> "00" 1 -> "01" 2 -> "02" else -> ""})

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

    override fun getViewModel() = StoragePathViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailStoragePathBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}


