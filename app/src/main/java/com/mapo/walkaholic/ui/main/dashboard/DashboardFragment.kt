package com.mapo.walkaholic.ui.main.dashboard

import android.content.ContentValues
import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.GridXy
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.setImageUrl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding, MainRepository>() {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
        private const val DASH_CHARACTER_RATE = 0.6051
        private const val CHARACTER_BETWEEN_CIRCLE_PADDING = 65
        private const val CHARACTER_EXP_CIRCLE_SIZE = 99
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            binding.user = _userResponse.value.data.first()
                            viewModel.getUserCharacterFilename(_userResponse.value.data.first().id)
                            viewModel.userCharacterFilenameResponse.observe(
                                viewLifecycleOwner,
                                Observer { _userCharacterResponse ->
                                    when (_userCharacterResponse) {
                                        is Resource.Success -> {
                                            when (_userCharacterResponse.value.code) {
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
                                                                                var animationDrawable =
                                                                                    AnimationDrawable()
                                                                                animationDrawable.isOneShot =
                                                                                    false
                                                                                _userCharacterResponse.value.data.forEachIndexed { _characterUriIndex, _characterUriElement ->
                                                                                    Glide.with(
                                                                                        requireContext()
                                                                                    )
                                                                                        .asBitmap()
                                                                                        .load("${viewModel!!.getResourceBaseUri()}${_characterUriElement.filename}")
                                                                                        .diskCacheStrategy(
                                                                                            DiskCacheStrategy.NONE
                                                                                        )
                                                                                        .skipMemoryCache(
                                                                                            true
                                                                                        )
                                                                                        .override(
                                                                                            (Target.SIZE_ORIGINAL.toFloat() * DASH_CHARACTER_RATE.toFloat()).toInt(),
                                                                                            (Target.SIZE_ORIGINAL.toFloat() * DASH_CHARACTER_RATE.toFloat()).toInt()
                                                                                        )
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
                                                                                                characterBitmap
                                                                                                animationDrawable.addFrame(
                                                                                                    characterBitmap,
                                                                                                    ANIMATION_DURATION
                                                                                                )
                                                                                                if (animationDrawable.numberOfFrames == _userCharacterResponse.value.data.size) {
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
                                                                                                            "#F9A25B"
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
                                                                                                        TypedValue.applyDimension(
                                                                                                            TypedValue.COMPLEX_UNIT_DIP,
                                                                                                            resource.width.toFloat(),
                                                                                                            GlobalApplication.getGlobalApplicationContext().resources.displayMetrics
                                                                                                        )
                                                                                                            .toInt()
                                                                                                    binding.dashIvCharacter.minimumHeight =
                                                                                                        TypedValue.applyDimension(
                                                                                                            TypedValue.COMPLEX_UNIT_DIP,
                                                                                                            resource.height.toFloat(),
                                                                                                            GlobalApplication.getGlobalApplicationContext().resources.displayMetrics
                                                                                                        )
                                                                                                            .toInt()
                                                                                                    binding.dashIvCharacter.setImageDrawable(
                                                                                                        animationDrawable
                                                                                                    )
                                                                                                    animationDrawable =
                                                                                                        binding.dashIvCharacter.drawable as AnimationDrawable
                                                                                                    animationDrawable.start()
                                                                                                }
                                                                                            }
                                                                                        })
                                                                                }
                                                                                viewModel!!.getCharacterUriList(
                                                                                    _userResponse.value.data.first().petId.toString()
                                                                                )
                                                                            }
                                                                            "400" -> {
                                                                                // Error
                                                                                Toast.makeText(
                                                                                    requireContext(),
                                                                                    getString(R.string.err_user),
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                //logout()
                                                                            }
                                                                            else -> {
                                                                                // Error
                                                                                Toast.makeText(
                                                                                    requireContext(),
                                                                                    getString(R.string.err_user),
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                //logout()
                                                                            }
                                                                        }
                                                                    }
                                                                    is Resource.Loading -> {
                                                                        // Loading
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
                                            handleApiError(_userCharacterResponse)
                                        }
                                    }
                                })
                        }
                        "400" -> {
                            // Error
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                            ).show()
                            //logout()
                            //requireActivity().startNewActivity(AuthActivity::class.java)
                        }
                        else -> {
                            // Error
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
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
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
        viewModel.sgisAccessTokenResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        Log.i(
                            ContentValues.TAG,
                            "현재 좌표 : ${GlobalApplication.currentLng} ${GlobalApplication.currentLat}"
                        )
                        viewModel.getTmCoord(
                            it.value.sgisAccessToken.accessToken,
                            GlobalApplication.currentLat,
                            GlobalApplication.currentLng
                        )
                    } else {
                        // Error
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(it)
                    Log.i(
                        ContentValues.TAG, "SGIS 인증 실패 : ${it.errorBody}"
                    )
                }
            }
        })
        viewModel.tmCoordResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        viewModel.getNearMsrstn(it.value.tmCoord.posX, it.value.tmCoord.posY)
                        Log.i(
                            ContentValues.TAG,
                            "TM 좌표 : ${it.value.tmCoord.posX} ${it.value.tmCoord.posY}"
                        )
                    } else {
                        // Error
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(it)
                    Log.i(
                        ContentValues.TAG, "TM 좌표 변환 실패 : ${it.errorBody}"
                    )
                }
            }
        })
        viewModel.nearMsrstnResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        viewModel.getWeatherDust(it.value.nearMsrstn.sidoName)
                        Log.i(
                            ContentValues.TAG,
                            "처리 결과 : ${it.value.nearMsrstn.sidoName} ${it.value.nearMsrstn.stationName}"
                        )
                        viewModel.weatherDustResponse.observe(viewLifecycleOwner, Observer { it2 ->
                            when (it2) {
                                is Resource.Success -> {
                                    if (!it2.value.error) {
                                        Log.i(
                                            ContentValues.TAG,
                                            "처리 결과 : ${it2.value.weatherDust} ${it2.value.weatherDust.singleOrNull { it3 -> it3.stationName == it.value.nearMsrstn.stationName }}"
                                        )
                                        binding.weatherDust =
                                            it2.value.weatherDust.singleOrNull { it3 -> it3.stationName == it.value.nearMsrstn.stationName }
                                        Log.i(
                                            ContentValues.TAG,
                                            "weatherDust 값 : ${binding.weatherDust}"
                                        )
                                        if (binding.weatherDust == null) {
                                            binding.weatherDust = it2.value.weatherDust.first()
                                        }
                                    } else {
                                        Log.i(
                                            ContentValues.TAG,
                                            "weatherDust 값 : ${binding.weatherDust}"
                                        )
                                    }
                                }
                                is Resource.Loading -> {
                                    // Loading
                                }
                                is Resource.Failure -> {
                                    // Network Error
                                    handleApiError(it2)
                                }
                            }
                        })
                    } else {
                        // Error
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(it)
                }
            }
        })
        viewModel.yesterdayWeatherResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.yesterdayWeather = it.value.yesterdayWeather
                        Log.i(
                            ContentValues.TAG, "Yesterday Weather : ${it.value.yesterdayWeather}"
                        )
                    } else {
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
            }
        })
        viewModel.todayWeatherResponse.observe(viewLifecycleOwner, Observer { _todayWeatherResponse ->
            when (_todayWeatherResponse) {
                is Resource.Success -> {
                    if (!_todayWeatherResponse.value.error) {
                        binding.todayWeather = _todayWeatherResponse.value.todayWeather
                        viewModel.getFilenameWeather(_todayWeatherResponse.value.todayWeather.weatherCode)
                        viewModel.filenameWeatherResponse.observe(
                            viewLifecycleOwner,
                            Observer { _filenameWeatherResponse ->
                                when (_filenameWeatherResponse) {
                                    is Resource.Success -> {
                                        when (_filenameWeatherResponse.value.code) {
                                            "200" -> {
                                                /*
                                                    날씨 코드 관련
                                                    1 맑음, 2 구름, 3 흐림, 4 비, 5 진눈개비, 6 눈, 7 오류
                                                */
                                                setImageUrl(
                                                    binding.dashIvWeather, _filenameWeatherResponse.value.data.first().weatherFilename
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
                                        handleApiError(_filenameWeatherResponse)
                                    }
                                }
                            })
                    } else {
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(_todayWeatherResponse)
                }
            }
        })
        viewModel.filenameThemeCategoryImageResponse.observe(
            viewLifecycleOwner,
            Observer { _filenameThemeCategoryImageResponse ->
                when (_filenameThemeCategoryImageResponse) {
                    is Resource.Success -> {
                        when (_filenameThemeCategoryImageResponse.value.code) {
                            "200" -> {
                                binding.dashRVTheme.also { _dashRVTheme ->
                                    val linearLayoutManager = LinearLayoutManager(requireContext())
                                    linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                                    _dashRVTheme.layoutManager = linearLayoutManager
                                    _dashRVTheme.setHasFixedSize(true)
                                    _dashRVTheme.adapter =
                                        DashboardThemeAdapter(_filenameThemeCategoryImageResponse.value.data)
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

                    }
                    is Resource.Failure -> {
                        // Network Error
                        handleApiError(_filenameThemeCategoryImageResponse) { viewModel.getFilenameThemeCategoryImage() }
                    }
                }
            })
        viewModel.getDash()
        viewModel.getFilenameThemeCategoryImage()
        viewModel.getSGISAccessToken()
        val tmp: GridXy = convertGRID_GPS(
            GlobalApplication.currentLng.toDouble(),
            GlobalApplication.currentLat.toDouble()
        )
        Log.e(">>", "x = " + tmp.x.toString() + ", y = " + tmp.y.toString())
        viewModel.getYesterdayWeather(tmp.x.toInt().toString(), tmp.y.toInt().toString())
        viewModel.getTodayWeather(tmp.x.toInt().toString(), tmp.y.toInt().toString())
        binding.dashIvSetting.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardFragmentDirections.actionActionBnvDashToActionBnvDashCharacterInfo()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
        binding.dashTvIntro.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardFragmentDirections.actionActionBnvDashToActionBnvDashProfile()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
        binding.dashLayoutWalkRecord.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardFragmentDirections.actionActionBnvDashToActionBnvDashWalkRecord()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
        binding.dashBtnWalkStart.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardFragmentDirections.actionActionBnvDashToActionBnvMap()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
        binding.dashTvcalorie.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardFragmentDirections.actionActionBnvDashToActionStoragePath()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
    }

    // 위도 경도 X, Y 좌표 변경
    private fun convertGRID_GPS(lat: Double, lng: Double): GridXy {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기준점 Y좌표(GRID)

        val DEGRAD: Double = Math.PI / 180.0

        Log.e(">>", DEGRAD.toString())

        val re: Double = RE / GRID
        val slat1: Double = SLAT1 * DEGRAD
        val slat2: Double = SLAT2 * DEGRAD
        val olon: Double = OLON * DEGRAD
        val olat: Double = OLAT * DEGRAD

        var sn: Double = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
        var sf: Double = tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = sf.pow(sn) * cos(slat1) / sn
        var ro: Double = tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / ro.pow(sn)

        var ra: Double = (tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5))
        ra = re * sf / ra.pow(sn)

        var theta: Double = lng * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        return GridXy(floor(ra * sin(theta) + XO + 0.5), floor(ro - ra * cos(theta) + YO + 0.5))
    }

    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}