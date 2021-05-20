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
import com.mapo.walkaholic.data.model.CharacterItemInfo
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
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardCharacterInfoFragment :
    BaseSharedFragment<DashboardCharacterInfoViewModel, FragmentDashboardCharacterInfoBinding, MainRepository>() {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
        private const val CHARACTER_SMALL_WIDTH = 69
        private const val CHARACTER_SMALL_HEIGHT = 86
        private const val CHARACTER_BETWEEN_CIRCLE_PADDING = PIXELS_PER_METRE * 30
        private const val CHARACTER_EXP_CIRCLE_SIZE = PIXELS_PER_METRE * 30
    }

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
        pagerAdapter.addFragment(DashboardCharacterInfoDetailFragment(0))
        pagerAdapter.addFragment(DashboardCharacterInfoDetailFragment(1))
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
                                                                                _userCharacterEquipStatusResponse.value.data.forEachIndexed { dataIndex, dataElement ->
                                                                                    if (dataElement.itemType == "hair") {
                                                                                        userCharacterEquipStatus["hair"] =
                                                                                            dataElement.itemId
                                                                                    } else if (dataElement.itemType == "face") {
                                                                                        userCharacterEquipStatus["face"] =
                                                                                            dataElement.itemId
                                                                                    }
                                                                                    if (dataIndex == _userCharacterEquipStatusResponse.value.data.size - 1) {
                                                                                        viewModel!!.getUserCharacterPreviewFilename(
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
                                                                                            },
                                                                                            _userResponse.value.data.first().id
                                                                                        )
                                                                                    }
                                                                                }
                                                                                viewModel!!.userCharacterPreviewFilenameResponse.observe(
                                                                                    viewLifecycleOwner,
                                                                                    Observer { _userCharacterPreviewFilenameResponse ->
                                                                                        when (_userCharacterPreviewFilenameResponse) {
                                                                                            is Resource.Success -> {
                                                                                                when (_userCharacterPreviewFilenameResponse.value.code) {
                                                                                                    "200" -> {
                                                                                                        _userCharacterPreviewFilenameResponse.value.data.forEachIndexed { filenameIndex, filenameElement ->
                                                                                                            var animationDrawable =
                                                                                                                AnimationDrawable()
                                                                                                            animationDrawable.isOneShot =
                                                                                                                false
                                                                                                            Glide.with(
                                                                                                                requireContext()
                                                                                                            )
                                                                                                                .asBitmap()
                                                                                                                .load(
                                                                                                                    "${viewModel!!.getResourceBaseUri()}${filenameElement.fileName}"
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
                                                                                                                            if (animationDrawable.numberOfFrames == _userCharacterPreviewFilenameResponse.value.data.size - 1) {
                                                                                                                                /*val charExp =
                                                                                                                                    (100.0 * (_userResponse.value.data.first().currentExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())
                                                                                                                                            / (_expInformationResponse.value.data.first().nextLevelNeedExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())).toLong()
                                                                                                                                val radius =
                                                                                                                                    CHARACTER_BETWEEN_CIRCLE_PADDING + PIXELS_PER_METRE * if (resource.width >= resource.height) resource.width / 2 else resource.height / 2
                                                                                                                                val bitmapInfoSheet =
                                                                                                                                    Bitmap.createBitmap(
                                                                                                                                        (radius * 2 + CHARACTER_EXP_CIRCLE_SIZE),
                                                                                                                                        (radius * 2 + CHARACTER_EXP_CIRCLE_SIZE),
                                                                                                                                        Bitmap.Config.ARGB_8888
                                                                                                                                    )
                                                                                                                                val canvasInfo =
                                                                                                                                    Canvas(
                                                                                                                                        bitmapInfoSheet
                                                                                                                                    )
                                                                                                                                val startAngle =
                                                                                                                                    135F
                                                                                                                                val sweepAngle =
                                                                                                                                    270F
                                                                                                                                val paint =
                                                                                                                                    Paint()
                                                                                                                                paint.isAntiAlias =
                                                                                                                                    true
                                                                                                                                paint.color =
                                                                                                                                    Color.parseColor(
                                                                                                                                        "#C9C9C9"
                                                                                                                                    )
                                                                                                                                paint.style =
                                                                                                                                    Paint.Style.FILL
                                                                                                                                var oval =
                                                                                                                                    RectF(
                                                                                                                                        0.toFloat(),
                                                                                                                                        0.toFloat(),
                                                                                                                                        canvasInfo.width.toFloat(),
                                                                                                                                        canvasInfo.height.toFloat()
                                                                                                                                    )
                                                                                                                                canvasInfo.drawArc(
                                                                                                                                    oval,
                                                                                                                                    startAngle,
                                                                                                                                    sweepAngle,
                                                                                                                                    true,
                                                                                                                                    paint
                                                                                                                                )
                                                                                                                                paint.color =
                                                                                                                                    Color.parseColor(
                                                                                                                                        "#D46544"
                                                                                                                                    )
                                                                                                                                canvasInfo.drawArc(
                                                                                                                                    oval,
                                                                                                                                    startAngle,
                                                                                                                                    2.7F * charExp,
                                                                                                                                    true,
                                                                                                                                    paint
                                                                                                                                )
                                                                                                                                paint.xfermode =
                                                                                                                                    PorterDuffXfermode(
                                                                                                                                        PorterDuff.Mode.CLEAR
                                                                                                                                    )
                                                                                                                                oval =
                                                                                                                                    RectF(
                                                                                                                                        ((canvasInfo.width / 2) - radius).toFloat(),
                                                                                                                                        ((canvasInfo.height / 2) - radius).toFloat(),
                                                                                                                                        ((canvasInfo.width / 2) + radius).toFloat(),
                                                                                                                                        ((canvasInfo.height / 2) + radius).toFloat()
                                                                                                                                    )
                                                                                                                                canvasInfo.drawArc(
                                                                                                                                    oval,
                                                                                                                                    startAngle,
                                                                                                                                    sweepAngle,
                                                                                                                                    true,
                                                                                                                                    paint
                                                                                                                                )
                                                                                                                                binding.dashIvCharacterInfo.setImageBitmap(
                                                                                                                                    bitmapInfoSheet
                                                                                                                                )
                                                                                                                                binding.dashIvCharacter.minimumWidth =
                                                                                                                                    resource.width * PIXELS_PER_METRE
                                                                                                                                binding.dashIvCharacter.minimumHeight =
                                                                                                                                    resource.height * PIXELS_PER_METRE
                                                                                                                                binding.dashIvCharacter.setImageDrawable(
                                                                                                                                    animationDrawable
                                                                                                                                )
                                                                                                                                animationDrawable =
                                                                                                                                    binding.dashIvCharacter.drawable as AnimationDrawable
                                                                                                                                animationDrawable.start()
*/
                                                                                                                                val charExp =
                                                                                                                                    (100.0 * (_userResponse.value.data.first().currentExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())
                                                                                                                                            / (_expInformationResponse.value.data.first().nextLevelNeedExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())).toLong()
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
                                                                                                )
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

                                                }
                                                else -> {

                                                }
                                            }
                                        }
                                        is Resource.Loading -> {

                                        }
                                        is Resource.Failure -> {
                                            handleApiError(_userCharacterEquipStatusResponse)
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
}