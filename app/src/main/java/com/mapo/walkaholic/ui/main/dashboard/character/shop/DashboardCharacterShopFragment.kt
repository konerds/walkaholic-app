package com.mapo.walkaholic.ui.main.dashboard.character.shop

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
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCharacterShopBinding
import com.mapo.walkaholic.ui.alertDialog
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterShopSlotClickListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterShopFragment :
    BaseFragment<DashboardCharacterShopViewModel, FragmentDashboardCharacterShopBinding, MainRepository>(),
    CharacterShopSlotClickListener {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
    }

    private var selectedSlotShopMapFace = mutableMapOf<Int, Triple<Boolean, ItemInfo, Boolean>>()
    private var selectedSlotShopMapHair = mutableMapOf<Int, Triple<Boolean, ItemInfo, Boolean>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val pagerAdapter =
            DashboardCharacterShopViewPagerAdapter(childFragmentManager, lifecycle, 2, this)
        binding.dashCharacterShopVP.adapter = pagerAdapter
        TabLayoutMediator(
            binding.dashCharacterShopTL,
            binding.dashCharacterShopVP
        ) { tab, position ->
            tab.text = when (position) {
                0 -> "얼굴"
                1 -> "머리"
                else -> ""
            }
        }.attach()
        binding.dashCharacterShopVP.isUserInputEnabled = false
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
                                                    with(binding) {
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
                                                                                                                                        binding.dashCharacterShopIvCharacter.minimumWidth =
                                                                                                                                            resource.width * PIXELS_PER_METRE
                                                                                                                                        binding.dashCharacterShopIvCharacter.minimumHeight =
                                                                                                                                            resource.height * PIXELS_PER_METRE
                                                                                                                                        binding.dashCharacterShopIvCharacter.setImageDrawable(
                                                                                                                                            animationDrawable
                                                                                                                                        )
                                                                                                                                        animationDrawable =
                                                                                                                                            binding.dashCharacterShopIvCharacter.drawable as AnimationDrawable
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
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            })
                                                                                    } else {

                                                                                    }
                                                                                }
                                                                            }
                                                                            else -> {
                                                                                // Error
                                                                                confirmDialog(
                                                                                    _expInformationResponse.value.message,
                                                                                    {
                                                                                        viewModel!!.getExpInformation(
                                                                                            _userResponse.value.data.first().id
                                                                                        )
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
                                                                        ) {
                                                                            viewModel!!.getExpInformation(
                                                                                _userResponse.value.data.first().id
                                                                            )
                                                                        }
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
                            binding.dashCharacterShopBtnBuy.setOnClickListener {
                                val totalPrice = selectedSlotShopMapFace.filter { it.value.first }
                                    .map { it.value.second.itemPrice!!.toInt() }
                                    .sum() + selectedSlotShopMapHair.filter { it.value.first }
                                    .map { it.value.second.itemPrice!!.toInt() }.sum()
                                if (totalPrice <= 0) {
                                    confirmDialog("구매하실 아이템을 선택하세요", null, null)
                                } else {
                                    val onClickConfirm: () -> Unit? = {
                                        val filteredSelectedFace =
                                            selectedSlotShopMapFace.filter { _selectedSlotShopMapFace -> _selectedSlotShopMapFace.value.first }
                                        val filteredSelectedHair =
                                            selectedSlotShopMapHair.filter { _selectedSlotShopMapHair -> _selectedSlotShopMapHair.value.first }
                                        if (filteredSelectedFace.isNullOrEmpty()
                                            && filteredSelectedHair.isNullOrEmpty()
                                        ) {
                                            confirmDialog("구매하실 아이템을 선택하세요", null, null)
                                        } else {
                                            val arrayListSelectedItemId = arrayListOf<Int?>()
                                            filteredSelectedFace.forEach { (_selectedSlotShopMapFaceIndex, _selectedSlotShopMapFaceElement) ->
                                                if (_selectedSlotShopMapFaceElement.second.itemId != null) {
                                                    arrayListSelectedItemId.add(
                                                        _selectedSlotShopMapFaceElement.second.itemId
                                                    )
                                                }
                                            }
                                            filteredSelectedHair.forEach { (_selectedSlotShopMapHairIndex, _selectedSlotShopMapHairElement) ->
                                                if (_selectedSlotShopMapHairElement.second.itemId != null) {
                                                    arrayListSelectedItemId.add(
                                                        _selectedSlotShopMapHairElement.second.itemId
                                                    )
                                                }
                                            }
                                            viewModel.buyItem(
                                                _userResponse.value.data.first().id,
                                                arrayListSelectedItemId
                                            )
                                            viewModel.buyItemResponse.observe(
                                                viewLifecycleOwner,
                                                Observer { _buyItemResponse ->
                                                    when (_buyItemResponse) {
                                                        is Resource.Success -> {
                                                            val stringAdditionalMessage =
                                                                if (!_buyItemResponse.value.data.isNullOrEmpty() && _buyItemResponse.value.data.size != 0) {
                                                                    "\n" + _buyItemResponse.value.data.first()
                                                                        .toString()
                                                                } else {
                                                                    ""
                                                                }
                                                            when (_buyItemResponse.value.code) {
                                                                "200" -> {
                                                                    confirmDialog(
                                                                        _buyItemResponse.value.message + stringAdditionalMessage,
                                                                        null,
                                                                        null
                                                                    )
                                                                    viewModel.getUser()
                                                                }
                                                                else -> {
                                                                    // Error
                                                                    confirmDialog(
                                                                        _buyItemResponse.value.message,
                                                                        {
                                                                            viewModel.buyItem(
                                                                                _userResponse.value.data.first().id,
                                                                                arrayListSelectedItemId
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
                                                            handleApiError(_buyItemResponse) {
                                                                viewModel.buyItem(
                                                                    _userResponse.value.data.first().id,
                                                                    arrayListSelectedItemId
                                                                )
                                                            }
                                                        }
                                                    }
                                                })
                                        }
                                    }
                                    alertDialog(
                                        "총 ${totalPrice}P 입니다. 구매하시겠습니까?",
                                        null,
                                        onClickConfirm as (() -> Unit)
                                    )
                                }
                            }
                        }
                        else -> {
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
                }
                is Resource.Failure -> {
                    handleApiError(_userResponse) { viewModel.getUser() }
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        binding.dashCharacterShopIvInfo.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardCharacterShopFragmentDirections.actionActionBnvDashCharacterShopToActionBnvDashCharacterInfo()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
    }

    override fun getViewModel() = DashboardCharacterShopViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardCharacterShopBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onItemClick(
        selectedSlotShopMap: MutableMap<Int, Triple<Boolean, ItemInfo, Boolean>>
    ) {
        if (selectedSlotShopMap[0]?.second?.itemType == "hair") {
            selectedSlotShopMapHair = selectedSlotShopMap
        } else if (selectedSlotShopMap[0]?.second?.itemType == "face") {
            selectedSlotShopMapFace = selectedSlotShopMap
        }
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            viewModel.getUserCharacterPreviewFilename(
                                _userResponse.value.data.first().id,
                                if (selectedSlotShopMapFace.filter { faceValue -> faceValue.value.third }
                                        .isNotEmpty()) {
                                    selectedSlotShopMapFace.filter { faceValue -> faceValue.value.third }.values.first().second.itemId.toString()
                                } else {
                                    if (selectedSlotShopMapFace.filter { faceValue -> faceValue.value.first }
                                            .isNotEmpty()) {
                                        selectedSlotShopMapFace.filter { faceValue -> faceValue.value.first }.values.first().second.itemId.toString()
                                    } else {
                                        ""
                                    }
                                },
                                if (selectedSlotShopMapHair.filter { hairValue -> hairValue.value.third }
                                        .isNotEmpty()) {
                                    selectedSlotShopMapHair.filter { hairValue -> hairValue.value.third }.values.first().second.itemId.toString()
                                } else {
                                    if (selectedSlotShopMapHair.filter { hairValue -> hairValue.value.first }
                                            .isNotEmpty()) {
                                        selectedSlotShopMapHair.filter { hairValue -> hairValue.value.first }.values.first().second.itemId.toString()
                                    } else {
                                        ""
                                    }
                                }
                            )
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

        if (selectedSlotShopMap[0]?.second?.itemType == "hair") {
            if ((selectedSlotShopMap.filter { it.value.first }.size + selectedSlotShopMapFace?.filter { it.value.first }!!.size) == 0) {
                binding.dashCharacterShopTvIntro1.text = "아이템을 구매해 보세요!"
                binding.dashCharacterShopTvIntro2.text = ""
                binding.dashCharacterShopTvIntro3.text = ""
                binding.dashCharacterShopTvIntro4.text = ""
                binding.dashCharacterShopTvIntro5.text = ""
                binding.dashCharacterShopTvIntro6.text = ""
            } else {
                binding.dashCharacterShopTvIntro1.text = "선택아이템("
                binding.dashCharacterShopTvIntro2.text =
                    (selectedSlotShopMap.filter { it.value.first }.size + selectedSlotShopMapFace?.filter { it.value.first }!!.size).toString()
                binding.dashCharacterShopTvIntro3.text = ") 총"
                binding.dashCharacterShopTvIntro4.text =
                    (selectedSlotShopMap.filter { it.value.first }
                        .map { it.value.second.itemPrice!!.toInt() }
                        .sum() + selectedSlotShopMapFace!!.filter { it.value.first }
                        .map { it.value.second.itemPrice!!.toInt() }.sum()).toString()
                binding.dashCharacterShopTvIntro5.text = "P"
                binding.dashCharacterShopTvIntro6.text = " 입니다."
            }
        } else if (selectedSlotShopMap[0]?.second?.itemType == "face") {
            if ((selectedSlotShopMap.filter { it.value.first }.size + selectedSlotShopMapHair?.filter { it.value.first }!!.size) == 0) {
                binding.dashCharacterShopTvIntro1.text = "아이템을 구매해 보세요!"
                binding.dashCharacterShopTvIntro2.text = ""
                binding.dashCharacterShopTvIntro3.text = ""
                binding.dashCharacterShopTvIntro4.text = ""
                binding.dashCharacterShopTvIntro5.text = ""
                binding.dashCharacterShopTvIntro6.text = ""
            } else {
                binding.dashCharacterShopTvIntro1.text = "선택아이템("
                binding.dashCharacterShopTvIntro2.text =
                    (selectedSlotShopMap.filter { it.value.first }.size + selectedSlotShopMapHair?.filter { it.value.first }!!.size).toString()
                binding.dashCharacterShopTvIntro3.text = ") 총"
                binding.dashCharacterShopTvIntro4.text =
                    (selectedSlotShopMap.filter { it.value.first }
                        .map { it.value.second.itemPrice!!.toInt() }
                        .sum() + selectedSlotShopMapHair!!.filter { it.value.first }
                        .map { it.value.second.itemPrice!!.toInt() }.sum()).toString()
                binding.dashCharacterShopTvIntro5.text = "P"
                binding.dashCharacterShopTvIntro6.text = " 입니다."
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navDirection: NavDirections? =
                    DashboardCharacterShopFragmentDirections.actionActionBnvDashCharacterShopToActionBnvDashCharacterInfo()
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