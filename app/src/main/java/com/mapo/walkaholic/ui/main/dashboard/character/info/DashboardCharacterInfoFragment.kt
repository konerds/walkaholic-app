package com.mapo.walkaholic.ui.main.dashboard.character.info

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
import com.mapo.walkaholic.databinding.FragmentDashboardCharacterInfoBinding
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterInventorySlotClickListener
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardCharacterInfoFragment :
    BaseSharedFragment<DashboardCharacterInfoViewModel, FragmentDashboardCharacterInfoBinding, MainRepository>(),
    CharacterInventorySlotClickListener {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
    }

    private var selectedSlotInfoMapFace = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()
    private var selectedSlotInfoMapHair = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel: DashboardCharacterInfoViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterInfoFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterInfoFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val pagerAdapter = DashboardCharacterInfoViewPagerAdapter(requireActivity())
        pagerAdapter.addFragment(DashboardCharacterInfoDetailFragment(0, this))
        pagerAdapter.addFragment(DashboardCharacterInfoDetailFragment(1, this))
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
                                                                                            _dataElement.itemId
                                                                                    } else if (_dataElement.itemType == "face") {
                                                                                        userCharacterEquipStatus["face"] =
                                                                                            _dataElement.itemId
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
        viewModel.getDash()
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
    ) = FragmentDashboardCharacterInfoBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onItemClick(
        selectedSlotInfoMap: MutableMap<Int, Pair<Boolean, ItemInfo>>
    ) {
        if (selectedSlotInfoMap[0]?.second?.itemType == "hair") {
            selectedSlotInfoMapHair = selectedSlotInfoMap
        } else if (selectedSlotInfoMap[0]?.second?.itemType == "face") {
            selectedSlotInfoMapFace = selectedSlotInfoMap
        }

        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            viewModel.getUserCharacterPreviewFilename(
                                _userResponse.value.data.first().id,
                                if (selectedSlotInfoMapFace.filter { faceValue -> faceValue.value.first }
                                        .isNotEmpty()) {
                                    selectedSlotInfoMapFace.filter { faceValue -> faceValue.value.first }.values.first().second.itemId.toString()
                                } else {
                                    ""
                                },
                                if (selectedSlotInfoMapHair.filter { hairValue -> hairValue.value.first }
                                        .isNotEmpty()) {
                                    selectedSlotInfoMapHair.filter { hairValue -> hairValue.value.first }.values.first().second.itemId.toString()
                                } else {
                                    ""
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
    }
}