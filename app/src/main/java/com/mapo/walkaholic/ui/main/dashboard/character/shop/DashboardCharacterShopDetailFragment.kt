package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailCharacterShopBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterItemSlotClickListener
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoDetailAdapter
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoViewModel
import com.mapo.walkaholic.ui.snackbar
import kotlinx.android.synthetic.main.fragment_dashboard_character_shop.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterShopDetailFragment(
    private val position: Int,
    private val listener : CharacterItemSlotClickListener
) : BaseSharedFragment<DashboardCharacterShopViewModel, FragmentDetailCharacterShopBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : DashboardCharacterShopViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        binding.viewModel = viewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopDetailFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatusShopSaleItem()
        viewModel.statusShopSaleItemResponse.observe(viewLifecycleOwner, Observer { _statusShopSaleItemResponse ->
            when(_statusShopSaleItemResponse) {
                is Resource.Success -> {
                    when(_statusShopSaleItemResponse.value.code) {
                        "200" -> {
                            binding.dashCharacterShopDetailRV.also { _dashCharacterShopDetailRV ->
                                val linearLayoutManager = LinearLayoutManager(requireContext())
                                linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                                _dashCharacterShopDetailRV.layoutManager = linearLayoutManager
                                _dashCharacterShopDetailRV.setHasFixedSize(true)
                                _dashCharacterShopDetailRV.adapter = when(position) {
                                    0 -> _statusShopSaleItemResponse.value.data.filter { _data -> _data.itemType == "face" } as ArrayList<ItemInfo>
                                    1 -> _statusShopSaleItemResponse.value.data.filter { _data -> _data.itemType == "hair" } as ArrayList<ItemInfo>
                                    else -> null
                                }?.let { arrayListFilteredItemInfo ->
                                    DashboardCharacterShopDetailAdapter(
                                        arrayListFilteredItemInfo, listener
                                    )
                                }
                            }
                        }
                        "400" -> {
                            // Error
                        }
                        else -> {
                            // Error
                        }
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_statusShopSaleItemResponse) { viewModel.getStatusShopSaleItem() }
                }
            }
        })
        binding.dashCharacterShopDetailRV.also { _dashCharacterShopDetailRV ->

        }
        binding.dashCharacterShopInitLayout.setOnClickListener {
            Log.d(ContentValues.TAG,"Click Init Button Event")
            //binding.dashCharacterInfoDetailRV
        }
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

    override fun getViewModel() = DashboardCharacterShopViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailCharacterShopBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}