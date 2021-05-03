package com.mapo.walkaholic.ui.main.dashboard

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.GRIDXY
import com.mapo.walkaholic.data.network.APISApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SGISApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardFragment :
        BaseFragment<DashboardViewModel, FragmentDashboardBinding, MainRepository>() {
    private var animCharacter: Animation? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        lifecycleScope.launch { viewModel.saveAuthToken() }
        viewModel.userResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.user = it.value.user
                    } else {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                        ).show()
                        logout()
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
                    logout()
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        viewModel.userCharacterResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.userCharacter = it.value.userCharacter
                        with(binding) {
                            viewModel!!.getExpTable(it.value.userCharacter.exp)
                            viewModel!!.expTableResponse.observe(viewLifecycleOwner, Observer { _exptable ->
                                when (_exptable) {
                                    is Resource.Success -> {
                                        if (!_exptable.value.error) {
                                            binding.expTable = _exptable.value.exptable
                                            binding.userCharacter?.let { userCharacter ->
                                                val charExp = (100.0 * (userCharacter.exp.toFloat() - _exptable.value.exptable.requireexp2.toFloat()) / (_exptable.value.exptable.requireexp1.toFloat() - _exptable.value.exptable.requireexp2.toFloat())).toLong()
                                                animCharacter = Animation(
                                                        63,
                                                        64,
                                                        2,
                                                        2,
                                                        (3.9).toLong(),
                                                        binding.dashSvCharacter.holder,
                                                        binding.dashIvCharacter,
                                                        charExp
                                                )
                                                when (userCharacter.type) {
                                                    0 -> {
                                                        animCharacter!!.setBitmapSheet(requireContext(),
                                                                R.drawable.img_character1)
                                                    }
                                                    1 -> {
                                                        animCharacter!!.setBitmapSheet(requireContext(),
                                                                R.drawable.img_character2)
                                                    }
                                                    2 -> {
                                                        animCharacter!!.setBitmapSheet(requireContext(),
                                                                R.drawable.img_character3)
                                                    }
                                                    else -> {
                                                        Toast.makeText(
                                                                requireContext(),
                                                                getString(R.string.err_user),
                                                                Toast.LENGTH_SHORT
                                                        ).show()
                                                        logout()
                                                    }
                                                }
                                                animCharacter!!.drawCharInfo()
                                                animCharacter!!.startThread()
                                            }
                                            val spannableTvWalkToday = dashTvWalkToday.text as Spannable
                                            spannableTvWalkToday.setSpan(
                                                    ForegroundColorSpan(Color.parseColor("#F97413")),
                                                    0,
                                                    5,
                                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                            )
                                        } else {
                                            Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.err_user),
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                            logout()
                                        }
                                    }
                                    is Resource.Failure -> {
                                        handleApiError(_exptable)
                                        Toast.makeText(
                                                requireContext(),
                                                getString(R.string.err_user),
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        logout()
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
                        logout()
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
                    logout()
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        viewModel.sgisAccessTokenResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        Log.i(
                                ContentValues.TAG, "현재 좌표 : ${GlobalApplication.currentLng} ${GlobalApplication.currentLat}"
                        )
                        viewModel.getTmCoord(it.value.sgisAccessToken.accessToken, GlobalApplication.currentLat, GlobalApplication.currentLng)
                    } else {
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
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
                                ContentValues.TAG, "TM 좌표 : ${it.value.tmCoord.posX} ${it.value.tmCoord.posY}"
                        )
                    } else {
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
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
                                ContentValues.TAG, "처리 결과 : ${it.value.nearMsrstn.sidoName} ${it.value.nearMsrstn.stationName}"
                        )
                        viewModel.weatherDustResponse.observe(viewLifecycleOwner, Observer { it2 ->
                            when (it2) {
                                is Resource.Success -> {
                                    if (!it2.value.error) {
                                        Log.i(
                                                ContentValues.TAG, "처리 결과 : ${it2.value.weatherDust} ${it2.value.weatherDust.singleOrNull { it3 -> it3.stationName == it.value.nearMsrstn.stationName }}"
                                        )
                                        binding.weatherDust = it2.value.weatherDust.singleOrNull { it3 -> it3.stationName == it.value.nearMsrstn.stationName }
                                        Log.i(
                                                ContentValues.TAG, "weatherDust 값 : ${binding.weatherDust}"
                                        )
                                        if (binding.weatherDust == null) {
                                            binding.weatherDust = it2.value.weatherDust.first()
                                        }
                                    } else {
                                        Log.i(
                                                ContentValues.TAG, "weatherDust 값 : ${binding.weatherDust}"
                                        )
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
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
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
        viewModel.todayWeatherResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.todayWeather = it.value.todayWeather
                        Log.i(
                                ContentValues.TAG, "Today Weather : ${it.value.todayWeather}"
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
        viewModel.getDash()
        viewModel.getSGISAccessToken()
        val tmp: GRIDXY = convertGRID_GPS(GlobalApplication.currentLng.toDouble(), GlobalApplication.currentLat.toDouble())
        Log.e(">>", "x = " + tmp.x.toString() + ", y = " + tmp.y.toString())
        viewModel.getYesterdayWeather(tmp.x.toInt().toString(),tmp.y.toInt().toString())
        viewModel.getTodayWeather(tmp.x.toInt().toString(),tmp.y.toInt().toString())
    }

    // 위도 경도 X, Y 좌표 변경
    private fun convertGRID_GPS(lat: Double, lng: Double) : GRIDXY
    {
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

        return GRIDXY(floor(ra * sin(theta) + XO + 0.5), floor(ro - ra * cos(theta) + YO + 0.5))
    }

    override fun onPause() {
        animCharacter?.interruptThread()
        super.onPause()
    }

    // @TODO Move To VM
    class Animation constructor(
            frameHeight: Int, frameWidth: Int,
            animFps: Int, private val frameCount: Int, private val pixelsPerMetre: Long, private val holder: SurfaceHolder, private val infoView: ImageView, private val charExp: Long
    ) : Runnable {
        private lateinit var animThread: Thread
        private lateinit var bitmapSheet: Bitmap
        private var charCanvas = Canvas()
        private val charPaint = Paint()
        private var currentFrame = 0
        private var frameTicker: Long
        private val framePeriod: Int = 1000 / animFps
        private val frameWidth: Int = (frameWidth.toLong() * pixelsPerMetre).toInt()
        private val frameHeight: Int = (frameHeight.toLong() * pixelsPerMetre).toInt()
        private fun getCurrentFrame(time: Long): Rect {
            if (time > frameTicker + framePeriod) {
                frameTicker = time
                currentFrame++
                if (currentFrame >= frameCount) {
                    currentFrame = 0
                }
            }
            val sourceRect = Rect(
                    Point(charCanvas.width / 2, charCanvas.height / 2).x - (this.frameWidth / 2),
                    Point(charCanvas.width / 2, charCanvas.height / 2).y - (this.frameHeight / 2),
                    Point(charCanvas.width / 2, charCanvas.height / 2).x + (this.frameWidth / 2),
                    Point(charCanvas.width / 2, charCanvas.height / 2).y + (this.frameHeight / 2))
            sourceRect.left = currentFrame * this.frameWidth
            sourceRect.right = sourceRect.left + this.frameWidth
            return sourceRect
        }

        private fun animate() {
            try {
                if (this.holder.surface.isValid) {
                    charCanvas = this.holder.lockCanvas()
                    charCanvas.drawColor(Color.parseColor("#FFFFFF"))
                    charPaint.color = Color.parseColor("#FFFFFF")
                    val sourceRect = getCurrentFrame(System.currentTimeMillis())
                    val destRect = Rect(
                            Point(charCanvas.width / 2, charCanvas.height / 2).x - (this.frameWidth / 2),
                            Point(charCanvas.width / 2, charCanvas.height / 2).y - (this.frameHeight / 2),
                            Point(charCanvas.width / 2, charCanvas.height / 2).x + (this.frameWidth / 2),
                            Point(charCanvas.width / 2, charCanvas.height / 2).y + (this.frameHeight / 2))
                    charCanvas.drawBitmap(bitmapSheet, sourceRect, destRect, charPaint)
                    this.holder.unlockCanvasAndPost(charCanvas)
                }
            } catch (e: Exception) {
                interruptThread()
            }
        }

        fun setBitmapSheet(context: Context?, bitmapResource: Int) {
            if (context != null) {
                this.bitmapSheet = BitmapFactory.decodeResource(context.resources, bitmapResource)
                this.bitmapSheet = Bitmap.createScaledBitmap(
                        bitmapSheet,
                        (this.frameWidth * frameCount),
                        this.frameHeight,
                        false
                )
            }
        }

        fun drawCharInfo() {
            val bitmapInfoSheet = Bitmap.createBitmap((140 * pixelsPerMetre).toInt(), (140 * pixelsPerMetre).toInt(), Bitmap.Config.ARGB_8888)
            val canvasInfo = Canvas(bitmapInfoSheet)
            val radius = 60 * pixelsPerMetre
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
            oval = RectF(((canvasInfo.width / 2) - radius).toFloat(), ((canvasInfo.height / 2) - radius).toFloat(), ((canvasInfo.width / 2) + radius).toFloat(), ((canvasInfo.height / 2) + radius).toFloat())
            canvasInfo.drawArc(oval, startAngle, sweepAngle, true, paint)
            infoView.setImageBitmap(bitmapInfoSheet)
            Log.i(
                    ContentValues.TAG, "${charExp}"
            )
        }

        fun startThread() {
            animThread = Thread(this)
            if (animThread != null && animThread.isInterrupted) {
                animThread.join()
            } else {
                animThread.start()
            }
            Log.i(
                    ContentValues.TAG, "${animThread.id} starting"
            )
        }

        fun interruptThread() {
            animThread.interrupt()
        }

        override fun run() {
            while (!this.holder.surfaceFrame.isEmpty) {
                animate()
            }
        }

        init {
            frameTicker = 0L
        }
    }

    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val accessToken = runBlocking { userPreferences.accessToken.first() }
        val api = remoteDataSource.buildRetrofitApi(InnerApi::class.java, accessToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(APISApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SGISApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}