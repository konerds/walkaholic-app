package com.mapo.walkaholic.ui.service

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
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.User
import com.mapo.walkaholic.data.model.UserCharacter
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.startNewActivity
import com.mapo.walkaholic.ui.visible
import java.lang.StringBuilder


class DashboardFragment :
        BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {
    private var animCharacter: Animation? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dashResponse.observe(viewLifecycleOwner, Observer {
            binding.dashProgressBar.visible(true)
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        updateUI(it.value.user, it.value.userCharacter, null, null, null)
                    } else {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.err_user),
                                Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().startNewActivity(AuthActivity::class.java)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    Toast.makeText(
                            requireContext(),
                            getString(R.string.err_user),
                            Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        binding.dashProgressBar.visible(false)
        viewModel.getDash()
    }

    override fun onPause() {
        animCharacter?.interruptThread()
        super.onPause()
    }

    private fun updateUI(
            user: User?,
            userCharacter: UserCharacter?,
            walkingRecord: Any?,
            weather: Any?,
            theme: Any?
    ) {
        with(binding) {
            val characterIntro = StringBuilder()
            characterIntro.append(
                    when (userCharacter?.type) {
                        0 -> "인간이 "
                        1 -> "오크가 "
                        2 -> "해골이 "
                        else -> "[오류] "
                    }
            )
            when (userCharacter?.type) {
                0 -> {
                    animCharacter = Animation(
                            63,
                            64,
                            10,
                            7,
                            4,
                            binding.dashSvCharacter.holder
                    )
                    animCharacter!!.setBitmapSheet(requireContext(),
                            R.drawable.img_character1)
                    animCharacter!!.startThread()
                }
                1 -> {
                    animCharacter = Animation(
                            63,
                            64,
                            10,
                            7,
                            4,
                            binding.dashSvCharacter.holder
                    )
                    animCharacter!!.setBitmapSheet(requireContext(),
                            R.drawable.img_character2)
                    animCharacter!!.startThread()
                }
                2 -> {
                    animCharacter = Animation(
                            63,
                            64,
                            10,
                            7,
                            4,
                            binding.dashSvCharacter.holder
                    )
                    animCharacter!!.setBitmapSheet(requireContext(),
                            R.drawable.img_character3)
                    animCharacter!!.startThread()
                }
                else -> {
                }
            }
            characterIntro.append("${user?.nickname}님을 기다렸어요!")
            if (user != null)
                dashTvIntro.text = characterIntro.toString().trim()
            val spannableTvWalkToday = dashTvWalkToday.text as Spannable
            spannableTvWalkToday.setSpan(
                    ForegroundColorSpan(Color.parseColor("#F97413")),
                    0,
                    5,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.dashProgressBar.visible(false)
    }

    inner class Animation constructor(
            frameHeight: Int, frameWidth: Int,
            animFps: Int, private val frameCount: Int, pixelsPerMetre: Int, holder: SurfaceHolder
    ) : Runnable {
        private lateinit var animThread: Thread
        private val holder: SurfaceHolder = holder
        private lateinit var bitmapSheet: Bitmap
        private var charCanvas = Canvas()
        private val charPaint = Paint()
        private val sourceRect: Rect
        private val destRect: Rect
        private var currentFrame = 0
        private var frameTicker: Long
        private val framePeriod: Int
        private val frameWidth: Int = (frameWidth * pixelsPerMetre)
        private val frameHeight: Int = (frameHeight * pixelsPerMetre)
        private fun getCurrentFrame(time: Long): Rect {
            if (time > frameTicker + framePeriod) {
                frameTicker = time
                currentFrame++
                if (currentFrame >= frameCount) {
                    currentFrame = 0
                }
            }
            sourceRect.left = currentFrame * this.frameWidth
            sourceRect.right = sourceRect.left + this.frameWidth
            return sourceRect
        }

        private fun animate() {
            if (this.holder.surface.isValid) {
                charCanvas = this.holder.lockCanvas()
                charCanvas.drawColor(Color.argb(255, 26, 128, 182))
                charPaint.color = Color.argb(255, 249, 129, 0)
                charPaint.textSize = 45F
                charCanvas.drawText("FPS : " + 1000, 20F, 40F, charPaint)
                getCurrentFrame(System.currentTimeMillis())
                charCanvas.drawBitmap(bitmapSheet, sourceRect, destRect, charPaint)
                this.holder.unlockCanvasAndPost(charCanvas)
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
            sourceRect = Rect(0, 0, this.frameWidth, this.frameHeight)
            destRect = Rect(0, 0, this.frameWidth, this.frameHeight)
            framePeriod = 1000 / animFps
            frameTicker = 0L
        }
    }

    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): DashboardRepository {
        //val id = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(Api::class.java)
        val apiWeather = remoteDataSource.buildApiDirect(Api::class.java, "http://WEATHER_REST")
        return DashboardRepository(api, apiWeather)
    }
}