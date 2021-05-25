package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailCharacterShopBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterShopSlotClickListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterShopDetailFragment(
    private val position: Int,
    private val listener : CharacterShopSlotClickListener
) : BaseFragment<DashboardCharacterShopDetailViewModel, FragmentDetailCharacterShopBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                        else -> {
                            // Error
                            confirmDialog(
                                _statusShopSaleItemResponse.value.message,
                                {
                                    viewModel.getStatusShopSaleItem()
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
                    handleApiError(_statusShopSaleItemResponse) { viewModel.getStatusShopSaleItem() }
                }
            }
        })
        binding.dashCharacterShopInitLayout.setOnClickListener { _dashCharacterShopInitLayout ->
            val adapter : DashboardCharacterShopDetailAdapter = binding.dashCharacterShopDetailRV.adapter as DashboardCharacterShopDetailAdapter
            adapter.clearSelectedItem()
            adapter.notifyDataSetChanged()
            listener.onItemClick(adapter.getData())
        }
    }

    override fun getViewModel() = DashboardCharacterShopDetailViewModel::class.java

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