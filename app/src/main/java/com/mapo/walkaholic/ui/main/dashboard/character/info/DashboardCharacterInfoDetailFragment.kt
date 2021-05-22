package com.mapo.walkaholic.ui.main.dashboard.character.info

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.CharacterItemInfo
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailCharacterInfoBinding
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterInventorySlotClickListener
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterInfoDetailFragment(
    private val position: Int,
    private val listener: CharacterInventorySlotClickListener
) : BaseSharedFragment<DashboardCharacterInfoViewModel, FragmentDetailCharacterInfoBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel: DashboardCharacterInfoViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterInfoDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterInfoDetailFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            viewModel.getStatusUserCharacterInventoryItem(_userResponse.value.data.first().id)
                            viewModel.statusUserCharacterInventoryItemResponse.observe(
                                viewLifecycleOwner,
                                Observer { _statusUserCharacterInventoryItem ->
                                    when (_statusUserCharacterInventoryItem) {
                                        is Resource.Success -> {
                                            when (_statusUserCharacterInventoryItem.value.code) {
                                                "200" -> {
                                                    viewModel.userCharacterEquipStatusResponse.observe(
                                                        viewLifecycleOwner,
                                                        Observer { _userCharacterEquipStatusResponse ->
                                                            when (_userCharacterEquipStatusResponse) {
                                                                is Resource.Success -> {
                                                                    when (_userCharacterEquipStatusResponse.value.code) {
                                                                        "200" -> {
                                                                            binding.dashCharacterInfoDetailRV.also { _dashCharacterInfoDetailRV ->
                                                                                val linearLayoutManager =
                                                                                    LinearLayoutManager(
                                                                                        requireContext()
                                                                                    )
                                                                                linearLayoutManager.orientation =
                                                                                    LinearLayoutManager.HORIZONTAL
                                                                                _dashCharacterInfoDetailRV.layoutManager =
                                                                                    linearLayoutManager
                                                                                _dashCharacterInfoDetailRV.setHasFixedSize(
                                                                                    true
                                                                                )
                                                                                when (position) {
                                                                                    0 -> {
                                                                                        val filterResult =
                                                                                            _statusUserCharacterInventoryItem.value.data.filter { _data -> _data.itemType == "face" } as ArrayList<ItemInfo>
                                                                                        when (filterResult.size) {
                                                                                            0 -> {
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                            }
                                                                                            1 -> {
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                            }
                                                                                            2 -> {
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                            }
                                                                                            else -> {
                                                                                            }
                                                                                        }
                                                                                        _dashCharacterInfoDetailRV.adapter =
                                                                                            DashboardCharacterInfoDetailAdapter(
                                                                                                filterResult,
                                                                                                listener
                                                                                            )
                                                                                    }
                                                                                    1 -> {
                                                                                        val filterResult =
                                                                                            _statusUserCharacterInventoryItem.value.data.filter { itemInfo -> itemInfo.itemType == "hair" } as ArrayList<ItemInfo>
                                                                                        when (filterResult.size) {
                                                                                            0 -> {
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                            }
                                                                                            1 -> {
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                            }
                                                                                            2 -> {
                                                                                                filterResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                            }
                                                                                            else -> {
                                                                                            }
                                                                                        }
                                                                                        _dashCharacterInfoDetailRV.adapter =
                                                                                            DashboardCharacterInfoDetailAdapter(
                                                                                                filterResult,
                                                                                                listener
                                                                                            )
                                                                                    }
                                                                                    else -> {
                                                                                    }
                                                                                }
                                                                                val adapter =
                                                                                    _dashCharacterInfoDetailRV.adapter as DashboardCharacterInfoDetailAdapter
                                                                                val initData =
                                                                                    mutableMapOf<Int, Pair<Boolean, ItemInfo>>()
                                                                                Log.e(TAG, "${adapter.getData()} \n${_userCharacterEquipStatusResponse.value.data}")
                                                                                adapter.getData()
                                                                                    .forEach { (_dataIndex1, _dataElement1) ->
                                                                                        _userCharacterEquipStatusResponse.value.data.forEachIndexed { _dataIndex2, _dataElement2 ->
                                                                                            if (_dataElement1.second.itemId.toString() == _dataElement2.itemId &&
                                                                                                _dataElement1.second.itemType == _dataElement2.itemType
                                                                                            ) {
                                                                                                initData[_dataIndex1] =
                                                                                                    Pair(
                                                                                                        true,
                                                                                                        _dataElement1.second
                                                                                                    )
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                adapter.setData(
                                                                                    initData
                                                                                )
                                                                                listener.onItemClick(
                                                                                    initData
                                                                                )
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
                                                                    handleApiError(
                                                                        _userCharacterEquipStatusResponse
                                                                    ) {
                                                                        viewModel.getUserCharacterEquipStatus(
                                                                            _userResponse.value.data.first().id
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        })
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
                                            handleApiError(_statusUserCharacterInventoryItem) {
                                                viewModel.getStatusUserCharacterInventoryItem(
                                                    _userResponse.value.data.first().id
                                                )
                                            }
                                        }
                                    }
                                })
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

                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_userResponse) { }
                }
            }
        })
        viewModel.getDash()
        binding.dashCharacterInfoInitLayout.setOnClickListener { _dashCharacterInfoInitLayout ->
            val adapter: DashboardCharacterInfoDetailAdapter =
                binding.dashCharacterInfoDetailRV.adapter as DashboardCharacterInfoDetailAdapter
            adapter.clearSelectedItem()
            adapter.notifyDataSetChanged()
            listener.onItemClick(adapter.getData())
        }
    }

    private fun showToastEvent(contents: String) {
        when (contents) {
            null -> {
            }
            "" -> {
            }
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
        when (contents) {
            null -> {
            }
            "" -> {
            }
            else -> {
                requireView().snackbar(contents)
            }
        }
    }

    override fun getViewModel() = DashboardCharacterInfoViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailCharacterInfoBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}