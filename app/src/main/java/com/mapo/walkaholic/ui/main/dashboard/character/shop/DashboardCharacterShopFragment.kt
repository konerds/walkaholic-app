package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.content.ContentValues.TAG
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.CharacterItemInfo
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCharacterShopBinding
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterItemSlotClickListener
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoViewPagerAdapter
import com.mapo.walkaholic.ui.snackbar
import kotlinx.android.synthetic.main.fragment_dashboard_character_shop.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardCharacterShopFragment :
    BaseSharedFragment<DashboardCharacterShopViewModel, FragmentDashboardCharacterShopBinding, MainRepository>(),
    CharacterItemSlotClickListener {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
        private const val CHARACTER_SMALL_WIDTH = 69
        private const val CHARACTER_SMALL_HEIGHT = 86
        private const val CHARACTER_BETWEEN_CIRCLE_PADDING = PIXELS_PER_METRE * 30
        private const val CHARACTER_EXP_CIRCLE_SIZE = PIXELS_PER_METRE * 30
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

        binding.userCharacterItem = CharacterItemInfo("1", "비타씨")

        val pagerAdapter = DashboardCharacterInfoViewPagerAdapter(requireActivity())
        pagerAdapter.addFragment(DashboardCharacterShopDetailFragment(0, this))
        pagerAdapter.addFragment(DashboardCharacterShopDetailFragment(1, this))
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
        /*viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            binding.user = _userResponse.value.data.first()
                            viewModel.getUserCharacterItem(_userResponse.value.data.first().petId.toString())
                            viewModel.characterItemResponse.observe(
                                viewLifecycleOwner,
                                Observer { _characterItemResponse ->
                                    when (_characterItemResponse) {
                                        is Resource.Success -> {
                                            if (!_characterItemResponse.value.error) {
                                                binding.userCharacterItem = _characterItemResponse.value.characterItem
                                            }
                                        }
                                        is Resource.Loading -> {

                                        }
                                        is Resource.Failure -> {
                                            handleApiError(_characterItemResponse)
                                        }
                                    }
                                })
                            with(binding) {
                                viewModel!!.getExpInformation(_userResponse.value.data.first().id)
                                viewModel!!.expInformationResponse.observe(
                                    viewLifecycleOwner,
                                    Observer { _expInformationResponse ->
                                        when (_expInformationResponse) {
                                            is Resource.Success -> {
                                                when (_expInformationResponse.value.code) {
                                                    "200" -> {
                                                        binding.expInformation = _expInformationResponse.value.data.first()
                                                        viewModel!!.getCharacterUriList(_userResponse.value.data.first().petId.toString())
                                                        viewModel!!.characterUriList.observe(
                                                            viewLifecycleOwner,
                                                            Observer { _characterUriList ->
                                                                when (_characterUriList) {
                                                                    is Resource.Success -> {
                                                                        if (!_characterUriList.value.error) {
                                                                            var animationDrawable =
                                                                                AnimationDrawable()
                                                                            animationDrawable.isOneShot =
                                                                                false
                                                                            _characterUriList.value.characterUri.forEachIndexed { _characterUriIndex, _characterUriElement ->
                                                                                Glide.with(requireContext())
                                                                                    .asBitmap()
                                                                                    .load(
                                                                                        viewModel!!.getResourceBaseUri() +
                                                                                                when(selectedSlotShopMapFace?.filter { faceValue -> faceValue.value.first }
                                                                                                    ?.get(0)?.second?.itemId) {
                                                                                                    "0" -> {
                                                                                                        "face" + selectedSlotShopMapFace!![0]!!.second!!.itemId
                                                                                                    }
                                                                                                    "1" -> {
                                                                                                        "face" + selectedSlotShopMapFace!![0]!!.second!!.itemId
                                                                                                    }
                                                                                                    "2" -> {
                                                                                                        "face" + selectedSlotShopMapFace!![0]!!.second!!.itemId
                                                                                                    }
                                                                                                    else -> { "" }
                                                                                                } +
                                                                                                when(selectedSlotShopMapHair?.filter { hairValue -> hairValue.value.first }
                                                                                                    ?.get(0)?.second?.itemId) {
                                                                                                    "0" -> {
                                                                                                        "face" + selectedSlotShopMapHair!![0]!!.second!!.itemId
                                                                                                    }
                                                                                                    "1" -> {
                                                                                                        "face" + selectedSlotShopMapHair!![0]!!.second!!.itemId
                                                                                                    }
                                                                                                    "2" -> {
                                                                                                        "face" + selectedSlotShopMapHair!![0]!!.second!!.itemId
                                                                                                    }
                                                                                                    else -> { "" }
                                                                                                } +
                                                                                                "${_characterUriElement.evolution_filename}.png")
                                                                                    .diskCacheStrategy(
                                                                                        DiskCacheStrategy.NONE
                                                                                    ).skipMemoryCache(true)
                                                                                    .into(object :
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
                                                                                            if (animationDrawable.numberOfFrames == _characterUriList.value.characterUri.size) {
                                                                                                *//*
                                                                                                val charExp =
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
                                                                                                val canvasInfo = Canvas(bitmapInfoSheet)
                                                                                                val startAngle = 135F
                                                                                                val sweepAngle = 270F
                                                                                                val paint = Paint()
                                                                                                paint.isAntiAlias = true
                                                                                                paint.color = Color.parseColor("#C9C9C9")
                                                                                                paint.style = Paint.Style.FILL
                                                                                                var oval = RectF(0.toFloat(), 0.toFloat(), canvasInfo.width.toFloat(), canvasInfo.height.toFloat())
                                                                                                canvasInfo.drawArc(oval, startAngle, sweepAngle, true, paint)
                                                                                                paint.color = Color.parseColor("#D46544")
                                                                                                canvasInfo.drawArc(oval, startAngle, 2.7F * charExp, true, paint)
                                                                                                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                                                                                                oval = RectF(((canvasInfo.width / 2) - radius).toFloat(),
                                                                                                        ((canvasInfo.height / 2) - radius).toFloat(),
                                                                                                        ((canvasInfo.width / 2) + radius).toFloat(),
                                                                                                        ((canvasInfo.height / 2) + radius).toFloat())
                                                                                                canvasInfo.drawArc(oval, startAngle, sweepAngle, true, paint)
                                                                                                binding.dashIvCharacterInfo.setImageBitmap(bitmapInfoSheet)
                                                                                                binding.dashIvCharacter.minimumWidth = resource.width * PIXELS_PER_METRE
                                                                                                binding.dashIvCharacter.minimumHeight = resource.height * PIXELS_PER_METRE
                                                                                                binding.dashIvCharacter.setImageDrawable(animationDrawable)
                                                                                                animationDrawable = binding.dashIvCharacter.drawable as AnimationDrawable
                                                                                                animationDrawable.start()
                                                                                                 *//*
                                                                                                val charExp =
                                                                                                    (100.0 * (_userResponse.value.data.first().currentExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())
                                                                                                            / (_expInformationResponse.value.data.first().nextLevelNeedExp.toFloat() - _expInformationResponse.value.data.first().currentLevelNeedExp.toFloat())).toLong()
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
                                                                    }
                                                                    is Resource.Loading -> {
                                                                    }
                                                                    is Resource.Failure -> {
                                                                        handleApiError(_characterUriList)
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
                                                handleApiError(_expInformationResponse)
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
        })*/
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

    override fun onRecyclerViewItemClick(
        view: View,
        position: Int,
        selectedSlotShopMap: MutableMap<Int, Triple<Boolean, ItemInfo, Boolean>>
    ) {
        if (selectedSlotShopMap[0]?.second?.itemType == "hair") {
            selectedSlotShopMapHair = selectedSlotShopMap
        } else if (selectedSlotShopMap[0]?.second?.itemType == "face") {
            selectedSlotShopMapFace = selectedSlotShopMap
        }
        Log.d(TAG, "Click Event From Shop Adapter")
        when (view.id) {
            R.id.itemShopLayout -> {
                Log.d(TAG, "Click Event In Shop Layout")
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
    }
}