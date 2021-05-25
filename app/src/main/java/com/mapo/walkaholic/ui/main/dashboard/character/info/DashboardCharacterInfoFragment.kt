package com.mapo.walkaholic.ui.main.dashboard.character.info

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCharacterInfoBinding
import com.mapo.walkaholic.ui.alertDialog
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterInventorySlotClickListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterInfoFragment :
    BaseFragment<DashboardCharacterInfoViewModel, FragmentDashboardCharacterInfoBinding, MainRepository>(),
    CharacterInventorySlotClickListener {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
    }

    private var selectedSlotInfoMapFace = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()
    private var selectedSlotInfoMapHair = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val pagerAdapter =
            DashboardCharacterInfoViewPagerAdapter(childFragmentManager, lifecycle, 2, this)
        binding.dashCharacterInfoVP.adapter = pagerAdapter
        TabLayoutMediator(
            binding.dashCharacterInfoTL,
            binding.dashCharacterInfoVP
        ) { tab, position ->
            tab.text = when (position) {
                0 -> "얼굴"
                1 -> "머리"
                else -> ""
            }
        }.attach()
        binding.dashCharacterInfoTL.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.dashCharacterInfoVP.currentItem = tab!!.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        binding.dashCharacterInfoVP.isUserInputEnabled = false
        viewModel.getUser()
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            binding.user = _userResponse.value.data.first()
                            viewModel.getUserCharacterEquipStatus(_userResponse.value.data.first().id)
                            viewModel.userCharacterEquipStatusResponse.observe(
                                viewLifecycleOwner,
                                Observer { _userCharacterEquipStatusResponse ->
                                    when (_userCharacterEquipStatusResponse) {
                                        is Resource.Success -> {
                                            when (_userCharacterEquipStatusResponse.value.code) {
                                                "200" -> {
                                                    viewModel!!.getExpInformation(_userResponse.value.data.first().id)
                                                    viewModel!!.expInformationResponse.observe(
                                                        viewLifecycleOwner,
                                                        Observer { _expInformationResponse ->
                                                            when (_expInformationResponse) {
                                                                is Resource.Success -> {
                                                                    when (_expInformationResponse.value.code) {
                                                                        "200" -> {
                                                                            binding.expInformation =
                                                                                _expInformationResponse.value.data.first()
                                                                            val userCharacterEquipStatus =
                                                                                mutableMapOf<String, String>()
                                                                            _userCharacterEquipStatusResponse.value.data.forEachIndexed { _dataIndex, _dataElement ->
                                                                                if (_dataElement.itemType == "hair") {
                                                                                    userCharacterEquipStatus["hair"] =
                                                                                        _dataElement.itemId.toString()
                                                                                } else if (_dataElement.itemType == "face") {
                                                                                    userCharacterEquipStatus["face"] =
                                                                                        _dataElement.itemId.toString()
                                                                                }
                                                                                if ((_dataIndex == _userCharacterEquipStatusResponse.value.data.size - 1) && _userCharacterEquipStatusResponse.value.data.size != 0) {
                                                                                    viewModel!!.getUserCharacterPreviewFilename(
                                                                                        _userResponse.value.data.first().id,
                                                                                        if (!userCharacterEquipStatus["face"].isNullOrEmpty()) {
                                                                                            userCharacterEquipStatus["face"]
                                                                                                .toString()
                                                                                        } else {
                                                                                            ""
                                                                                        },
                                                                                        if (!userCharacterEquipStatus["hair"].isNullOrEmpty()) {
                                                                                            userCharacterEquipStatus["hair"]
                                                                                                .toString()
                                                                                        } else {
                                                                                            ""
                                                                                        }
                                                                                    )
                                                                                    viewModel!!.userCharacterPreviewFilenameResponse.observe(
                                                                                        viewLifecycleOwner,
                                                                                        Observer { _userCharacterPreviewFilenameResponse ->
                                                                                            when (_userCharacterPreviewFilenameResponse) {
                                                                                                is Resource.Success -> {
                                                                                                    when (_userCharacterPreviewFilenameResponse.value.code) {
                                                                                                        "200" -> {
                                                                                                            var animationDrawable =
                                                                                                                AnimationDrawable()
                                                                                                            animationDrawable.isOneShot =
                                                                                                                false
                                                                                                            _userCharacterPreviewFilenameResponse.value.data.forEachIndexed { _filenameIndex, _filenameElement ->
                                                                                                                Glide.with(
                                                                                                                    requireContext()
                                                                                                                )
                                                                                                                    .asBitmap()
                                                                                                                    .load(
                                                                                                                        "${viewModel!!.getResourceBaseUri()}${_filenameElement.filename}"
                                                                                                                    )
                                                                                                                    .diskCacheStrategy(
                                                                                                                        DiskCacheStrategy.NONE
                                                                                                                    )
                                                                                                                    .skipMemoryCache(
                                                                                                                        true
                                                                                                                    )
                                                                                                                    .into(
                                                                                                                        object :
                                                                                                                            CustomTarget<Bitmap>() {
                                                                                                                            override fun onLoadCleared(
                                                                                                                                placeholder: Drawable?
                                                                                                                            ) {
                                                                                                                            }

                                                                                                                            override fun onResourceReady(
                                                                                                                                resource: Bitmap,
                                                                                                                                transition: Transition<in Bitmap>?
                                                                                                                            ) {
                                                                                                                                val characterBitmap =
                                                                                                                                    BitmapDrawable(
                                                                                                                                        resource
                                                                                                                                    )
                                                                                                                                animationDrawable.addFrame(
                                                                                                                                    characterBitmap,
                                                                                                                                    ANIMATION_DURATION
                                                                                                                                )
                                                                                                                                if (animationDrawable.numberOfFrames == _userCharacterPreviewFilenameResponse.value.data.size) {
                                                                                                                                    /*val charExp =
                                                                                                                                        (100.0 * (_userResponse.value.data.first().currentExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())
                                                                                                                                                / (_expInformationResponse.value.data.first().nextLevelNeedExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())).toLong()*/
                                                                                                                                    binding.dashCharacterInfoIvCharacter.minimumWidth =
                                                                                                                                        resource.width * PIXELS_PER_METRE
                                                                                                                                    binding.dashCharacterInfoIvCharacter.minimumHeight =
                                                                                                                                        resource.height * PIXELS_PER_METRE
                                                                                                                                    binding.dashCharacterInfoIvCharacter.setImageDrawable(
                                                                                                                                        animationDrawable
                                                                                                                                    )
                                                                                                                                    animationDrawable =
                                                                                                                                        binding.dashCharacterInfoIvCharacter.drawable as AnimationDrawable
                                                                                                                                    animationDrawable.start()
                                                                                                                                }
                                                                                                                            }
                                                                                                                        })
                                                                                                            }
                                                                                                        }
                                                                                                        else -> {
                                                                                                            // Error
                                                                                                            confirmDialog(
                                                                                                                _userCharacterPreviewFilenameResponse.value.message,
                                                                                                                {
                                                                                                                    viewModel!!.getUserCharacterPreviewFilename(
                                                                                                                        _userResponse.value.data.first().id,
                                                                                                                        if (!userCharacterEquipStatus["face"].isNullOrEmpty()) {
                                                                                                                            userCharacterEquipStatus.get(
                                                                                                                                "face"
                                                                                                                            )
                                                                                                                                .toString()
                                                                                                                        } else {
                                                                                                                            ""
                                                                                                                        },
                                                                                                                        if (!userCharacterEquipStatus["hair"].isNullOrEmpty()) {
                                                                                                                            userCharacterEquipStatus.get(
                                                                                                                                "hair"
                                                                                                                            )
                                                                                                                                .toString()
                                                                                                                        } else {
                                                                                                                            ""
                                                                                                                        }
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
                                                                                                        _userCharacterPreviewFilenameResponse
                                                                                                    ) {
                                                                                                        viewModel!!.getUserCharacterPreviewFilename(
                                                                                                            _userResponse.value.data.first().id,
                                                                                                            if (!userCharacterEquipStatus["face"].isNullOrEmpty()) {
                                                                                                                userCharacterEquipStatus.get(
                                                                                                                    "face"
                                                                                                                )
                                                                                                                    .toString()
                                                                                                            } else {
                                                                                                                ""
                                                                                                            },
                                                                                                            if (!userCharacterEquipStatus["hair"].isNullOrEmpty()) {
                                                                                                                userCharacterEquipStatus.get(
                                                                                                                    "hair"
                                                                                                                )
                                                                                                                    .toString()
                                                                                                            } else {
                                                                                                                ""
                                                                                                            }
                                                                                                        )
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        })
                                                                                } else {

                                                                                }
                                                                            }
                                                                        }
                                                                        else -> {
                                                                            confirmDialog(
                                                                                _expInformationResponse.value.message,
                                                                                {
                                                                                    viewModel!!.getExpInformation(_userResponse.value.data.first().id)
                                                                                },
                                                                                "재시도"
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                                is Resource.Failure -> {
                                                                    // Network Error
                                                                    handleApiError(
                                                                        _expInformationResponse
                                                                    ) { viewModel!!.getExpInformation(_userResponse.value.data.first().id) }
                                                                }
                                                            }
                                                        })
                                                    binding.dashCharacterInfoBtnEquip.setOnClickListener {
                                                        val filteredSelectedFace =
                                                            selectedSlotInfoMapFace.filter { _selectedSlotInfoMapFace -> _selectedSlotInfoMapFace.value.first }
                                                        val filteredSelectedHair =
                                                            selectedSlotInfoMapHair.filter { _selectedSlotInfoMapHair -> _selectedSlotInfoMapHair.value.first }
                                                        viewModel.equipItem(
                                                            _userResponse.value.data.first().id,
                                                            if (filteredSelectedFace.filter { faceValue -> faceValue.value.first }
                                                                    .isNullOrEmpty()) {
                                                                null
                                                            } else {
                                                                filteredSelectedFace.values.first().second.itemId
                                                            },
                                                            if (filteredSelectedHair.filter { hairValue -> hairValue.value.first }
                                                                    .isNullOrEmpty()) {
                                                                null
                                                            } else {
                                                                filteredSelectedHair.values.first().second.itemId
                                                            }
                                                        )
                                                        viewModel.equipItemResponse.observe(
                                                            viewLifecycleOwner,
                                                            Observer { _equipItemResponse ->
                                                                when (_equipItemResponse) {
                                                                    is Resource.Success -> {
                                                                        when (_equipItemResponse.value.code) {
                                                                            "200" -> {
                                                                                showToastEvent(_equipItemResponse.value.message)
                                                                                val navDirection: NavDirections? =
                                                                                    DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoSelf()
                                                                                if (navDirection != null) {
                                                                                    findNavController().navigate(navDirection)
                                                                                }
                                                                            }
                                                                            else -> {
                                                                                // Error
                                                                                confirmDialog(
                                                                                    _equipItemResponse.value.message,
                                                                                    {
                                                                                        viewModel.equipItem(
                                                                                            _userResponse.value.data.first().id,
                                                                                            if (filteredSelectedFace.filter { faceValue -> faceValue.value.first }
                                                                                                    .isNullOrEmpty()) {
                                                                                                null
                                                                                            } else {
                                                                                                filteredSelectedFace.values.first().second.itemId
                                                                                            },
                                                                                            if (filteredSelectedHair.filter { hairValue -> hairValue.value.first }
                                                                                                    .isNullOrEmpty()) {
                                                                                                null
                                                                                            } else {
                                                                                                filteredSelectedHair.values.first().second.itemId
                                                                                            }
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
                                                                        handleApiError(_equipItemResponse) { viewModel.equipItem(
                                                                            _userResponse.value.data.first().id,
                                                                            if (filteredSelectedFace.filter { faceValue -> faceValue.value.first }
                                                                                    .isNullOrEmpty()) {
                                                                                null
                                                                            } else {
                                                                                filteredSelectedFace.values.first().second.itemId
                                                                            },
                                                                            if (filteredSelectedHair.filter { hairValue -> hairValue.value.first }
                                                                                    .isNullOrEmpty()) {
                                                                                null
                                                                            } else {
                                                                                filteredSelectedHair.values.first().second.itemId
                                                                            }
                                                                        ) }
                                                                    }
                                                                }
                                                            })
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
                                            handleApiError(_userCharacterEquipStatusResponse) {
                                                viewModel.getUserCharacterEquipStatus(
                                                    _userResponse.value.data.first().id
                                                )
                                            }
                                        }
                                    }
                                })
                        }
                        else -> {
                            confirmDialog(
                                _userResponse.value.message,
                                {
                                    viewModel.getUser()
                                },
                                "재시도"
                            )
                            //requireActivity().startNewActivity(AuthActivity::class.java)
                        }
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    handleApiError(_userResponse) { viewModel.getUser() }
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        binding.dashCharacterInfoIvShop.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoToActionBnvDashCharacterShop()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
        binding.dashCharacterInfoTvIntro.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoToActionBnvDashCharacterShop()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
    }

    override fun getViewModel() = DashboardCharacterInfoViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardCharacterInfoBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onItemClick(
        selectedSlotInfoMap: MutableMap<Int, Pair<Boolean, ItemInfo>>, isClear: Boolean, selectedReverseInfoMap: MutableMap<Int, Pair<Boolean, ItemInfo>>
    ) {
        if (selectedSlotInfoMap[0]?.second?.itemType == "hair") {
            selectedSlotInfoMapHair = selectedSlotInfoMap
            selectedSlotInfoMapFace = selectedReverseInfoMap
        } else if (selectedSlotInfoMap[0]?.second?.itemType == "face") {
            selectedSlotInfoMapFace = selectedSlotInfoMap
            selectedSlotInfoMapHair = selectedReverseInfoMap
        }
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            viewModel.userCharacterEquipStatusResponse.observe(
                                viewLifecycleOwner,
                                Observer { _userCharacterEquipStatusResponse ->
                                    when (_userCharacterEquipStatusResponse) {
                                        is Resource.Success -> {
                                            when (_userCharacterEquipStatusResponse.value.code) {
                                                "200" -> {
                                                    viewModel.getUserCharacterPreviewFilename(
                                                        _userResponse.value.data.first().id,
                                                        if (isClear || selectedSlotInfoMapFace.isEmpty()) {
                                                            _userCharacterEquipStatusResponse.value.data[1].itemId.toString()
                                                        } else {
                                                            if (!selectedSlotInfoMapFace.filter { faceValue -> faceValue.value.first }
                                                                    .values.isNullOrEmpty()) {
                                                                selectedSlotInfoMapFace.filter { faceValue -> faceValue.value.first }.values.first().second.itemId.toString()
                                                            } else {
                                                                ""
                                                            }
                                                        },
                                                        if (isClear || selectedSlotInfoMapHair.isEmpty()) {
                                                            _userCharacterEquipStatusResponse.value.data[0].itemId.toString()
                                                        } else {
                                                            if (!selectedSlotInfoMapHair.filter { hairValue -> hairValue.value.first }
                                                                    .values.isNullOrEmpty()) {
                                                                selectedSlotInfoMapHair.filter { hairValue -> hairValue.value.first }.values.first().second.itemId.toString()
                                                            } else {
                                                                ""
                                                            }
                                                        }
                                                    )
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _userCharacterEquipStatusResponse.value.message,
                                                        {
                                                            viewModel.getUserCharacterEquipStatus(_userResponse.value.data.first().id)
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
                                            handleApiError(_userCharacterEquipStatusResponse) { viewModel.getUserCharacterEquipStatus(_userResponse.value.data.first().id) }
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

    override fun discardItem(itemId: Int, itemName: String) {
        val onClickYes: () -> Unit? = {
            viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
                when (_userResponse) {
                    is Resource.Success -> {
                        when (_userResponse.value.code) {
                            "200" -> {
                                viewModel.deleteItem(_userResponse.value.data.first().id, itemId.toString())
                                viewModel.deleteItemResponse.observe(viewLifecycleOwner, Observer { _deleteItemResponse ->
                                    when(_deleteItemResponse) {
                                        is Resource.Success -> {
                                            when(_deleteItemResponse.value.code) {
                                                "200" -> {
                                                    showToastEvent(_deleteItemResponse.value.message)
                                                    val navDirection: NavDirections? =
                                                        DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoSelf()
                                                    if (navDirection != null) {
                                                        findNavController().navigate(navDirection)
                                                    }
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _deleteItemResponse.value.message,
                                                        {
                                                            viewModel.deleteItem(_userResponse.value.data.first().id, itemId.toString())
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
                                            handleApiError(_deleteItemResponse) { viewModel.deleteItem(_userResponse.value.data.first().id, itemId.toString()) }
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
        alertDialog("$itemName 아이템을 버리시겠습니까?", null, onClickYes as (() -> Unit))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navDirection: NavDirections? = DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoToActionBnvDash()
                    if (navDirection != null) {
                        findNavController().navigate(navDirection)
                    }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}