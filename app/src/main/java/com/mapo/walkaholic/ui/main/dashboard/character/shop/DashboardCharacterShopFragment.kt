package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.SparseBooleanArray
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
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterItemSlotClickListener
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoViewPagerAdapter
import com.mapo.walkaholic.ui.snackbar
import kotlinx.android.synthetic.main.fragment_dashboard_character_shop.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardCharacterShopFragment :
    BaseSharedFragment<DashboardCharacterShopViewModel, FragmentDashboardCharacterShopBinding, MainRepository>(), CharacterItemSlotClickListener {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
        private const val CHARACTER_SMALL_WIDTH = 69
        private const val CHARACTER_SMALL_HEIGHT = 86
        private const val CHARACTER_BETWEEN_CIRCLE_PADDING = PIXELS_PER_METRE * 30
        private const val CHARACTER_EXP_CIRCLE_SIZE = PIXELS_PER_METRE * 30
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : DashboardCharacterShopViewModel by viewModels {
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
        val pagerAdapter = DashboardCharacterInfoViewPagerAdapter(requireActivity())
        pagerAdapter.addFragment(DashboardCharacterShopDetailFragment(0))
        pagerAdapter.addFragment(DashboardCharacterShopDetailFragment(1))
        binding.dashCharacterShopVP.adapter = pagerAdapter
        TabLayoutMediator(binding.dashCharacterShopTL, binding.dashCharacterShopVP) { tab, position ->
            tab.text = when(position) {
                0 -> "얼굴"
                1 -> "머리"
                else -> ""
            }
        }.attach()
        viewModel.onClickEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopFragment::onClickEvent)
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
                                                    Observer { it2 ->
                                                        when (it2) {
                                                            is Resource.Success -> {
                                                                if (!it2.value.error) {
                                                                    var animationDrawable =
                                                                        AnimationDrawable()
                                                                    animationDrawable.isOneShot =
                                                                        false
                                                                    it2.value.characterUri.forEachIndexed { index1, s ->
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
                                                                                    if (animationDrawable.numberOfFrames == it2.value.characterUri.size) {
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
                                                                handleApiError(it2)
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
                    null
                }
                else -> {
                    null
                }
            }
        if (navDirection != null) {
            findNavController().navigate(navDirection)
        }
    }

    private fun showToastEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
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
        when(contents) {
            null -> { }
            "" -> { }
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

    override fun onRecyclerViewItemClick(view: View, position: Int, itemInfo: ArrayList<ItemInfo>, selectedItems: SparseBooleanArray, selectedTotalPrice: Int) {
        when(view.id) {
            R.id.itemShopLayout -> {
                binding.dashCharacterShopTvIntro1.text = selectedItems.size().toString()
                binding.dashCharacterShopTvIntro2.text = selectedTotalPrice.toString()
            }
        }
    }
}