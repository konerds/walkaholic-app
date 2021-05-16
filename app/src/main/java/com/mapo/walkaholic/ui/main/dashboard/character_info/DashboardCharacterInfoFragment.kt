package com.mapo.walkaholic.ui.main.dashboard.character_info

import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCharacterInfoBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardCharacterInfoFragment :
    BaseFragment<DashboardCharacterInfoViewModel, FragmentDashboardCharacterInfoBinding, MainRepository>() {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
        private const val CHARACTER_SMALL_WIDTH = 69
        private const val CHARACTER_SMALL_HEIGHT = 86
        private const val CHARACTER_BETWEEN_CIRCLE_PADDING = PIXELS_PER_METRE * 30
        private const val CHARACTER_EXP_CIRCLE_SIZE = PIXELS_PER_METRE * 30
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        val pagerAdapter = DashboardCharacterInfoViewPagerAdapter(requireActivity())
        pagerAdapter.addFragment(DashboardCharacterInfoDetailFragment(0))
        pagerAdapter.addFragment(DashboardCharacterInfoDetailFragment(1))
        binding.dashCharacterInfoVP.adapter = pagerAdapter
        TabLayoutMediator(binding.dashCharacterInfoTL, binding.dashCharacterInfoVP) { tab, position ->
            tab.text = when(position) {
                0 -> "얼굴"
                1 -> "머리"
                else -> ""
            }
        }.attach()
        viewModel.onClickEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterInfoFragment::onClickEvent)
        )
        viewModel.userResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.user = it.value.user
                        viewModel.getUserCharacterItem(it.value.user.character_id.toString())
                        viewModel.characterItemResponse.observe(
                            viewLifecycleOwner,
                            Observer { it2 ->
                                when (it2) {
                                    is Resource.Success -> {
                                        if (!it2.value.error) {
                                            binding.userCharacterItem = it2.value.characterItem
                                        }
                                    }
                                    is Resource.Loading -> {

                                    }
                                    is Resource.Failure -> {
                                        handleApiError(it2)
                                    }
                                }
                            })
                        with(binding) {
                            viewModel!!.getExpTable(it.value.user.user_current_exp)
                            viewModel!!.expTableResponse.observe(
                                viewLifecycleOwner,
                                Observer { _exptable ->
                                    when (_exptable) {
                                        is Resource.Success -> {
                                            if (!_exptable.value.error) {
                                                binding.expTable = _exptable.value.exptable
                                                viewModel!!.getCharacterUriList(it.value.user.character_id.toString())
                                                viewModel!!.characterUriList.observe(
                                                    viewLifecycleOwner,
                                                    Observer { it3 ->
                                                        when (it3) {
                                                            is Resource.Success -> {
                                                                if (!it3.value.error) {
                                                                    var animationDrawable =
                                                                        AnimationDrawable()
                                                                    animationDrawable.isOneShot =
                                                                        false
                                                                    it3.value.characterUri.forEachIndexed { index1, s ->
                                                                        Glide.with(requireContext())
                                                                            .asBitmap()
                                                                            .load("${viewModel!!.getResourceBaseUri()}${s.evolution_filename}")
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
                                                                                    if (animationDrawable.numberOfFrames == it3.value.characterUri.size) {
                                                                                        /*
                                                                                        val charExp =
                                                                                            (100.0 * (userCharacter.exp.toFloat() - _exptable.value.exptable.requireexp2.toFloat())
                                                                                                    / (_exptable.value.exptable.requireexp1.toFloat() - _exptable.value.exptable.requireexp2.toFloat())).toLong()
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
                                                                                         */
                                                                                        val charExp =
                                                                                            (100.0 * (it.value.user.user_current_exp.toFloat() - _exptable.value.exptable.requireexp2.toFloat())
                                                                                                    / (_exptable.value.exptable.requireexp1.toFloat() - _exptable.value.exptable.requireexp2.toFloat())).toLong()
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
                                                            }
                                                            is Resource.Loading -> {
                                                            }
                                                            is Resource.Failure -> {
                                                                handleApiError(it3)
                                                            }
                                                        }
                                                    })
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.err_user),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                //logout()
                                            }
                                        }
                                        is Resource.Failure -> {
                                            handleApiError(_exptable)
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
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_user),
                            Toast.LENGTH_SHORT
                        ).show()
                        //logout()
                        //requireActivity().startNewActivity(AuthActivity::class.java)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    handleApiError(it)
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
    }

    private fun onClickEvent(name: String) {
        val navDirection: NavDirections? =
            when (name) {
                "shop" -> {
                    DashboardCharacterInfoFragmentDirections.actionActionBnvDashCharacterInfoToActionBnvDashCharacterShop()
                }
                else -> {
                    null
                }
            }
        if (navDirection != null) {
            findNavController().navigate(navDirection)
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
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}