package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCharacterShopBinding
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterShopSlotClickListener
import com.mapo.walkaholic.ui.snackbar
import kotlinx.android.synthetic.main.fragment_dashboard_character_shop.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardCharacterShopFragment :
    BaseSharedFragment<DashboardCharacterShopViewModel, FragmentDashboardCharacterShopBinding, MainRepository>(),
    CharacterShopSlotClickListener {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
    }

    private var selectedSlotShopMapFace = mutableMapOf<Int, Triple<Boolean, ItemInfo, Boolean>>()
    private var selectedSlotShopMapHair = mutableMapOf<Int, Triple<Boolean, ItemInfo, Boolean>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel: DashboardCharacterShopViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopFragment::showToastEvent)
        )
        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopFragment::showSnackbarEvent)
        )
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
                                                                            "400" -> {
                                                                                Toast.makeText(
                                                                                    requireContext(),
                                                                                    getString(R.string.err_user),
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                //logout()
                                                                            }
                                                                            else -> {
                                                                                Toast.makeText(
                                                                                    requireContext(),
                                                                                    getString(R.string.err_user),
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                //logout()
                                                                            }
                                                                        }
                                                                    }
                                                                    is Resource.Failure -> {
                                                                        // Network Error
                                                                        handleApiError(
                                                                            _expInformationResponse
                                                                        )
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            getString(R.string.err_user),
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        //logout()
                                                                    }
                                                                }
                                                            })
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
                                            handleApiError(_userCharacterEquipStatusResponse) {
                                                viewModel.getUserCharacterEquipStatus(
                                                    _userResponse.value.data.first().id
                                                )
                                            }
                                        }
                                    }
                                })
                            binding.dashCharacterShopBtnBuy.setOnClickListener {
                                val filteredSelectedFace = selectedSlotShopMapFace.filter { _selectedSlotShopMapFace -> _selectedSlotShopMapFace.value.first }
                                val filteredSelectedHair = selectedSlotShopMapHair.filter { _selectedSlotShopMapHair -> _selectedSlotShopMapHair.value.first }
                                if (filteredSelectedFace.isNullOrEmpty()
                                    && filteredSelectedHair.isNullOrEmpty()) {
                                    showToastEvent("구매하실 아이템을 선택하세요")
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
                                }
                            }
                        }
                        "400" -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                            ).show()
                            //logout()
                            //requireActivity().startNewActivity(AuthActivity::class.java)
                        }
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                            ).show()
                            //logout()
                            //requireActivity().startNewActivity(AuthActivity::class.java)
                        }
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    handleApiError(_userResponse)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_user),
                        Toast.LENGTH_SHORT
                    ).show()
                    //logout()
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        viewModel.buyItemResponse.observe(
            viewLifecycleOwner,
            Observer { _buyItemResponse ->
                when (_buyItemResponse) {
                    is Resource.Success -> {
                        when (_buyItemResponse.value.code) {
                            "200" -> {
                                showToastEvent(_buyItemResponse.value.message)
                                viewModel.getDash()
                            }
                            "400" -> {
                                // Error
                                showToastEvent(_buyItemResponse.value.message)
                            }
                            else -> {
                                // Error
                                showToastEvent(_buyItemResponse.value.message)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        // Loading
                    }
                    is Resource.Failure -> {
                        handleApiError(_buyItemResponse)
                    }
                }
            })
        viewModel.getDash()
        binding.dashCharacterShopIvInfo.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardCharacterShopFragmentDirections.actionActionBnvDashCharacterShopToActionBnvDashCharacterInfo()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
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
                    handleApiError(_userResponse)
                }
            }
        })

        if (selectedSlotShopMap[0]?.second?.itemType == "hair") {
            binding.dashCharacterShopTvIntro1.text =
                (selectedSlotShopMap.filter { it.value.first }.size + selectedSlotShopMapFace?.filter { it.value.first }!!.size).toString()
            binding.dashCharacterShopTvIntro2.text =
                (selectedSlotShopMap.filter { it.value.first }
                    .map { it.value.second.itemPrice!!.toInt() }
                    .sum() + selectedSlotShopMapFace!!.filter { it.value.first }
                    .map { it.value.second.itemPrice!!.toInt() }.sum()).toString()
        } else if (selectedSlotShopMap[0]?.second?.itemType == "face") {
            binding.dashCharacterShopTvIntro1.text =
                (selectedSlotShopMap.filter { it.value.first }.size + selectedSlotShopMapHair?.filter { it.value.first }!!.size).toString()
            binding.dashCharacterShopTvIntro2.text =
                (selectedSlotShopMap.filter { it.value.first }
                    .map { it.value.second.itemPrice!!.toInt() }
                    .sum() + selectedSlotShopMapHair!!.filter { it.value.first }
                    .map { it.value.second.itemPrice!!.toInt() }.sum()).toString()
        }
    }
}