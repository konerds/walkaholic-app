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
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.mapo.walkaholic.data.model.GridXy
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
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

        viewModel.getUser()
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            binding.user = _userResponse.value.data.first()
                            viewModel.getUserCharacterFilename(_userResponse.value.data.first().id)
                            viewModel.userCharacterFilenameResponse.observe(
                                viewLifecycleOwner,
                                Observer { _userCharacterFilenameResponse ->
                                    when (_userCharacterFilenameResponse) {
                                        is Resource.Success -> {
                                            when (_userCharacterFilenameResponse.value.code) {
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
                                                                                _userCharacterFilenameResponse.value.data.forEachIndexed { _characterUriIndex, _characterUriElement ->
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
                                                                                                if (animationDrawable.numberOfFrames == _userCharacterFilenameResponse.value.data.size) {
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
                                                                        ) {
                                                                            viewModel!!.getExpInformation(
                                                                                _userResponse.value.data.first().id
                                                                            )
                                                                        }
                                                                        //logout()
                                                                    }
                                                                }
                                                            })
                                                    }
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _userCharacterFilenameResponse.value.message,
                                                        {
                                                            viewModel.getUserCharacterFilename(
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
                                            handleApiError(_userCharacterFilenameResponse) {
                                                viewModel.getUserCharacterFilename(
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
                            //requireActivity().startNewActivity(AuthActivity::class.java)
                        }
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_userResponse) { viewModel.getUser() }
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        viewModel.getSGISAccessToken()
        viewModel.sgisAccessTokenResponse.observe(
            viewLifecycleOwner,
            Observer { _sgisAccessTokenResponse ->
                when (_sgisAccessTokenResponse) {
                    is Resource.Success -> {
                        if (!_sgisAccessTokenResponse.value.error) {
                            viewModel.getTmCoord(
                                _sgisAccessTokenResponse.value.sgisAccessToken.accessToken,
                                GlobalApplication.currentLat,
                                GlobalApplication.currentLng
                            )
                            viewModel.tmCoordResponse.observe(
                                viewLifecycleOwner,
                                Observer { _tmCoordResponse ->
                                    when (_tmCoordResponse) {
                                        is Resource.Success -> {
                                            if (!_tmCoordResponse.value.error) {
                                                viewModel.getNearMsrstn(
                                                    _tmCoordResponse.value.tmCoord.posX,
                                                    _tmCoordResponse.value.tmCoord.posY
                                                )
                                                viewModel.nearMsrstnResponse.observe(
                                                    viewLifecycleOwner,
                                                    Observer { _nearMsrstnResponse ->
                                                        when (_nearMsrstnResponse) {
                                                            is Resource.Success -> {
                                                                if (!_nearMsrstnResponse.value.error) {
                                                                    viewModel.getWeatherDust(
                                                                        _nearMsrstnResponse.value.nearMsrstn.sidoName
                                                                    )
                                                                    viewModel.weatherDustResponse.observe(
                                                                        viewLifecycleOwner,
                                                                        Observer { _weatherDustResponse ->
                                                                            when (_weatherDustResponse) {
                                                                                is Resource.Success -> {
                                                                                    if (!_weatherDustResponse.value.error) {
                                                                                        binding.weatherDust =
                                                                                            _weatherDustResponse.value.weatherDust.singleOrNull { it3 -> it3.stationName == _nearMsrstnResponse.value.nearMsrstn.stationName }
                                                                                        if (binding.weatherDust == null) {
                                                                                            binding.weatherDust =
                                                                                                _weatherDustResponse.value.weatherDust.first()
                                                                                        }
                                                                                    } else {
                                                                                        confirmDialog(
                                                                                            "공공 API 미세먼지 데이터를 호출하는 데 실패하였습니다",
                                                                                            {
                                                                                                viewModel.getWeatherDust(
                                                                                                    _nearMsrstnResponse.value.nearMsrstn.sidoName
                                                                                                )
                                                                                            },
                                                                                            "재시도"
                                                                                        )
                                                                                    }
                                                                                }
                                                                                is Resource.Loading -> {
                                                                                    // Loading
                                                                                }
                                                                                is Resource.Failure -> {
                                                                                    // Network Error
                                                                                    handleApiError(
                                                                                        _weatherDustResponse
                                                                                    ) {
                                                                                        viewModel.getWeatherDust(
                                                                                            _nearMsrstnResponse.value.nearMsrstn.sidoName
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        })
                                                                } else {
                                                                    // Error
                                                                    confirmDialog(
                                                                        "공공 API 미세먼지 데이터를 호출하는 데 실패하였습니다",
                                                                        {
                                                                            viewModel.getNearMsrstn(
                                                                                _tmCoordResponse.value.tmCoord.posX,
                                                                                _tmCoordResponse.value.tmCoord.posY
                                                                            )
                                                                        },
                                                                        "재시도"
                                                                    )
                                                                }
                                                            }
                                                            is Resource.Loading -> {
                                                                // Loading
                                                            }
                                                            is Resource.Failure -> {
                                                                // Network Error
                                                                handleApiError(_nearMsrstnResponse) {
                                                                    viewModel.getNearMsrstn(
                                                                        _tmCoordResponse.value.tmCoord.posX,
                                                                        _tmCoordResponse.value.tmCoord.posY
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    })
                                            } else {
                                                // Error
                                                confirmDialog(
                                                    "통계청 API 데이터를 호출하는 데 실패하였습니다",
                                                    {
                                                        viewModel.getTmCoord(
                                                            _sgisAccessTokenResponse.value.sgisAccessToken.accessToken,
                                                            GlobalApplication.currentLat,
                                                            GlobalApplication.currentLng
                                                        )
                                                    },
                                                    "재시도"
                                                )
                                            }
                                        }
                                        is Resource.Loading -> {
                                            // Loading
                                        }
                                        is Resource.Failure -> {
                                            // Network Error
                                            handleApiError(_tmCoordResponse) {
                                                viewModel.getTmCoord(
                                                    _sgisAccessTokenResponse.value.sgisAccessToken.accessToken,
                                                    GlobalApplication.currentLat,
                                                    GlobalApplication.currentLng
                                                )
                                            }
                                        }
                                    }
                                })
                        } else {
                            // Error
                            confirmDialog(
                                "통계청 API 데이터를 호출하는 데 실패하였습니다",
                                {
                                    viewModel.getSGISAccessToken()
                                },
                                "재시도"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Loading
                    }
                    is Resource.Failure -> {
                        // Network Error
                        handleApiError(_sgisAccessTokenResponse) { viewModel.getSGISAccessToken() }
                    }
                }
            })
        viewModel.getFilenameThemeCategoryImage()
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
                            else -> {
                                // Error
                                confirmDialog(
                                    _filenameThemeCategoryImageResponse.value.message,
                                    {
                                        viewModel.getFilenameThemeCategoryImage()
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
                        handleApiError(_filenameThemeCategoryImageResponse) { viewModel.getFilenameThemeCategoryImage() }
                    }
                }
            })
        val nXy: GridXy = convertGridGps(
            GlobalApplication.currentLng.toDouble(),
            GlobalApplication.currentLat.toDouble()
        )
        Log.e(">>", "x = " + nXy.x.toString() + ", y = " + nXy.y.toString())
        viewModel.getYesterdayWeather(nXy.x.toInt().toString(), nXy.y.toInt().toString())
        viewModel.yesterdayWeatherResponse.observe(
            viewLifecycleOwner,
            Observer { _yesterdayWeatherResponse ->
                when (_yesterdayWeatherResponse) {
                    is Resource.Success -> {
                        if (!_yesterdayWeatherResponse.value.error) {
                            binding.yesterdayWeather =
                                _yesterdayWeatherResponse.value.yesterdayWeather
                        } else {
                            // Error
                            confirmDialog(
                                "공공 API 날씨 데이터를 호출하는 데 실패하였습니다",
                                {
                                    viewModel.getTodayWeather(
                                        nXy.x.toInt().toString(), nXy.y.toInt().toString()
                                    )
                                },
                                "재시도"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Loading
                    }
                    is Resource.Failure -> {
                        // Network Error
                        handleApiError(_yesterdayWeatherResponse) {
                            viewModel.getYesterdayWeather(
                                nXy.x.toInt().toString(),
                                nXy.y.toInt().toString()
                            )
                        }
                    }
                }
            })
        viewModel.getTodayWeather(nXy.x.toInt().toString(), nXy.y.toInt().toString())
        viewModel.todayWeatherResponse.observe(
            viewLifecycleOwner,
            Observer { _todayWeatherResponse ->
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
                                                        binding.dashIvWeather,
                                                        _filenameWeatherResponse.value.data.first().weatherFilename
                                                    )
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _filenameWeatherResponse.value.message,
                                                        {
                                                            viewModel.getFilenameWeather(
                                                                _todayWeatherResponse.value.todayWeather.weatherCode
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
                                            handleApiError(_filenameWeatherResponse) {
                                                viewModel.getFilenameWeather(_todayWeatherResponse.value.todayWeather.weatherCode)
                                            }
                                        }
                                    }
                                })
                        } else {
                            confirmDialog(
                                "공공 API 날씨 데이터를 호출하는 데 실패하였습니다",
                                {
                                    viewModel.getTodayWeather(
                                        nXy.x.toInt().toString(), nXy.y.toInt().toString()
                                    )
                                },
                                "재시도"
                            )
                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        handleApiError(_todayWeatherResponse) {
                            viewModel.getTodayWeather(
                                nXy.x.toInt().toString(), nXy.y.toInt().toString()
                            )
                        }
                    }
                }
            })
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
    }

    // 위도 경도 X, Y 좌표 변경
    private fun convertGridGps(lat: Double, lng: Double): GridXy {
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