package com.mapo.walkaholic.ui.main.dashboard.character.info

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
import com.mapo.walkaholic.databinding.FragmentDetailCharacterInfoBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterInventorySlotClickListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterInfoDetailFragment(
    private val position: Int,
    private val listener: CharacterInventorySlotClickListener
) : BaseFragment<DashboardCharacterInfoDetailViewModel, FragmentDetailCharacterInfoBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUser()
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
                                                    viewModel.getUserCharacterEquipStatus(
                                                        _userResponse.value.data.first().id
                                                    )
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
                                                                                var filterReverseResult: ArrayList<ItemInfo>? =
                                                                                    null
                                                                                when (position) {
                                                                                    0 -> {
                                                                                        val filterResult =
                                                                                            _statusUserCharacterInventoryItem.value.data.filter { _data -> _data.itemType == "face" } as ArrayList<ItemInfo>
                                                                                        filterReverseResult =
                                                                                            _statusUserCharacterInventoryItem.value.data.filter { _data -> _data.itemType == "hair" } as ArrayList<ItemInfo>
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
                                                                                                filterReverseResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterReverseResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterReverseResult.add(
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
                                                                                                filterReverseResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterReverseResult.add(
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
                                                                                                filterReverseResult.add(
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
                                                                                                filterReverseResult,
                                                                                                listener
                                                                                            )
                                                                                    }
                                                                                    1 -> {
                                                                                        val filterResult =
                                                                                            _statusUserCharacterInventoryItem.value.data.filter { itemInfo -> itemInfo.itemType == "hair" } as ArrayList<ItemInfo>
                                                                                        filterReverseResult =
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
                                                                                                filterReverseResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterReverseResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterReverseResult.add(
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
                                                                                                filterReverseResult.add(
                                                                                                    ItemInfo(
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null
                                                                                                    )
                                                                                                )
                                                                                                filterReverseResult.add(
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
                                                                                                filterReverseResult.add(
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
                                                                                                filterReverseResult,
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
                                                                                val initReverseData =
                                                                                    mutableMapOf<Int, Pair<Boolean, ItemInfo>>()
                                                                                adapter.getData()
                                                                                    .forEach { (_dataIndex1, _dataElement1) ->
                                                                                        _userCharacterEquipStatusResponse.value.data.forEachIndexed { _dataIndex2, _dataElement2 ->
                                                                                            if (_dataElement1.second.itemId.toString() == _dataElement2.itemId.toString() &&
                                                                                                _dataElement1.second.itemType == _dataElement2.itemType &&
                                                                                                _dataElement1.second.itemType == (if (position == 0) {
                                                                                                    "face"
                                                                                                } else {
                                                                                                    "hair"
                                                                                                })
                                                                                            ) {
                                                                                                initData[_dataIndex1] =
                                                                                                    Pair(
                                                                                                        true,
                                                                                                        _dataElement1.second
                                                                                                    )
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                adapter.getReverseData()
                                                                                    .forEach { (_dataIndex1, _dataElement1) ->
                                                                                        _userCharacterEquipStatusResponse.value.data.forEachIndexed { _dataIndex2, _dataElement2 ->
                                                                                            if (_dataElement1.second.itemId.toString() == _dataElement2.itemId.toString() &&
                                                                                                _dataElement1.second.itemType == _dataElement2.itemType &&
                                                                                                _dataElement1.second.itemType == (if (position == 0) {
                                                                                                    "hair"
                                                                                                } else {
                                                                                                    "face"
                                                                                                })
                                                                                            ) {
                                                                                                initReverseData[_dataIndex1] =
                                                                                                    Pair(
                                                                                                        true,
                                                                                                        _dataElement1.second
                                                                                                    )
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                if (initData.filter { _initData -> !((_initData.value.second.itemType == "face") || (_initData.value.second.itemType == "hair")) }
                                                                                        .isNullOrEmpty()) {
                                                                                    if (initReverseData.filter { _initReverseData -> !((_initReverseData.value.second.itemType == "face") || (_initReverseData.value.second.itemType == "hair")) }
                                                                                            .isNullOrEmpty()) {
                                                                                        adapter.setData(
                                                                                            initData
                                                                                        )
                                                                                        adapter.setReverseData(
                                                                                            initReverseData
                                                                                        )
                                                                                        listener.onItemClick(
                                                                                            adapter.getData(),
                                                                                            true,
                                                                                            adapter.getReverseData()
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        else -> {
                                                                            // Error
                                                                            confirmDialog(
                                                                                _userCharacterEquipStatusResponse.value.message,
                                                                                {
                                                                                    viewModel.getUserCharacterEquipStatus(
                                                                                        _userResponse.value.data.first().id
                                                                                    )
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
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _statusUserCharacterInventoryItem.value.message,
                                                        {
                                                            viewModel.getStatusUserCharacterInventoryItem(
                                                                _userResponse.value.data.first().id
                                                            )
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
                                            handleApiError(_statusUserCharacterInventoryItem) {
                                                viewModel.getStatusUserCharacterInventoryItem(
                                                    _userResponse.value.data.first().id
                                                )
                                            }
                                        }
                                    }
                                })
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _userResponse.value.message,
                                {
                                    viewModel.getUser()
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
                    handleApiError(_userResponse) { viewModel.getUser() }
                }
            }
        })
        binding.dashCharacterInfoInitLayout.setOnClickListener { _dashCharacterInfoInitLayout ->
            initInventory()
        }
    }

    private fun initInventory() {
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            viewModel.getUserCharacterEquipStatus(
                                _userResponse.value.data.first().id
                            )
                            viewModel.userCharacterEquipStatusResponse.observe(
                                viewLifecycleOwner,
                                Observer { _userCharacterEquipStatusResponse ->
                                    when (_userCharacterEquipStatusResponse) {
                                        is Resource.Success -> {
                                            when (_userCharacterEquipStatusResponse.value.code) {
                                                "200" -> {
                                                    binding.dashCharacterInfoDetailRV.also { _dashCharacterInfoDetailRV ->
                                                        val adapter =
                                                            _dashCharacterInfoDetailRV.adapter as DashboardCharacterInfoDetailAdapter
                                                        val initData =
                                                            mutableMapOf<Int, Pair<Boolean, ItemInfo>>()
                                                        adapter.getData()
                                                            .forEach { (_dataIndex1, _dataElement1) ->
                                                                _userCharacterEquipStatusResponse.value.data.forEachIndexed { _dataIndex2, _dataElement2 ->
                                                                    if (_dataElement1.second.itemId.toString() == _dataElement2.itemId.toString() &&
                                                                        _dataElement1.second.itemType == _dataElement2.itemType &&
                                                                        _dataElement1.second.itemType == (if (position == 0) {
                                                                            "face"
                                                                        } else {
                                                                            "hair"
                                                                        })
                                                                    ) {
                                                                        initData[_dataIndex1] =
                                                                            Pair(
                                                                                true,
                                                                                _dataElement1.second
                                                                            )
                                                                    }
                                                                }
                                                            }
                                                        if (initData.filter { _initData -> !((_initData.value.second.itemType == "face") || (_initData.value.second.itemType == "hair")) }
                                                                .isNullOrEmpty()) {
                                                            adapter.setData(
                                                                initData
                                                            )
                                                            listener.onItemClick(
                                                                adapter.getData(),
                                                                true,
                                                                adapter.getReverseData()
                                                            )
                                                        }
                                                    }
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _userCharacterEquipStatusResponse.value.message,
                                                        {
                                                            viewModel.getUserCharacterEquipStatus(
                                                                _userResponse.value.data.first().id
                                                            )
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
                                            handleApiError(
                                                _userCharacterEquipStatusResponse
                                            ) { viewModel.getUserCharacterEquipStatus(
                                                _userResponse.value.data.first().id
                                            ) }
                                        }
                                    }
                                })
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _userResponse.value.message,
                                {
                                    viewModel.getUser()
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
                    handleApiError(_userResponse) { viewModel.getUser() }
                }
            }
        })
    }

    override fun getViewModel() = DashboardCharacterInfoDetailViewModel::class.java

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