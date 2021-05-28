package com.mapo.walkaholic.ui.main.map

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationListener
import com.mapo.walkaholic.data.model.MapFacilities
import com.mapo.walkaholic.data.model.response.ThemeCourseResponse
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentMapBinding
import com.mapo.walkaholic.ui.alertDialog
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.visible
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MainRepository>(),
    OnMapReadyCallback, LocationListener, MapFacilitiesClickListener, SensorEventListener {
    private lateinit var mapView: MapView
    private lateinit var mMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    val mapArgs: MapFragmentArgs by navArgs()

    private var time = 0
    private var timerTask: Timer? = null

    private var mSteps = 0
    private var mPrevSteps = 0
    private var mPauseSteps = 0

    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null

    private val arrayListMapFacilities = arrayListOf<MapFacilities>()

    val arrayListToiletMarker = arrayListOf<Marker>()
    val arrayListStoreMarker = arrayListOf<Marker>()
    val arrayListCafeMarker = arrayListOf<Marker>()
    val arrayListPharmacyMarker = arrayListOf<Marker>()
    val arrayListBicycleMarker = arrayListOf<Marker>()
    val arrayListPoliceMarker = arrayListOf<Marker>()

    // 0 : Stop / 1 : Playing / 2 : Pause
    private var walkProcess: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.walkNum = "0"
        binding.walkTime = "00:00:00"
        binding.isWalk = mapArgs.isWalk
        if (mapArgs.themeId == -1) {
            binding.themeCourse = ThemeCourseResponse.DataThemeCourse(
                -1,
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류",
                "오류"
            )
        }
        locationSource =
            FusedLocationSource(
                this,
                GlobalApplication.LOCATION_PERMISSION_REQUEST_CODE
            )
        binding.mapWalkControllerLayout.setOnTouchListener { v, event ->
            true
        }
        binding.mapWalkControllerLayout2.setOnTouchListener { v, event ->
            true
        }
        binding.mapView.getMapAsync(this)
        when (mapArgs.isWalk) {
            true -> {
                binding.mapWalkControllerLayout.visible(true)
                binding.mapWalkControllerLayout2.visible(false)
                binding.mapNavigationLayout.visible(true)
                binding.mapThemeCourseLayout.visible(false)
                binding.mapViewWalkRecord.setBackgroundColor(Color.parseColor("#F37520"))
                binding.mapViewFacilities.setBackgroundColor(Color.parseColor("#E3E0DB"))
                binding.mapViewCourse.setBackgroundColor(Color.parseColor("#E3E0DB"))
                binding.mapTvWalkRecord.setTextColor(Color.parseColor("#F37520"))
                binding.mapTvFacilities.setTextColor(Color.parseColor("#E3E0DB"))
                binding.mapTvCourse.setTextColor(Color.parseColor("#E3E0DB"))
                binding.mapTvWalkRecord.mapTvWalkRecord.isSelected = true
                binding.mapNavigationLayoutWalkRecord.visible(true)
                binding.mapTvWalkRecord.setOnClickListener {
                    binding.mapViewWalkRecord.setBackgroundColor(Color.parseColor("#F37520"))
                    binding.mapViewFacilities.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewCourse.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvWalkRecord.setTextColor(Color.parseColor("#F37520"))
                    binding.mapTvFacilities.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvCourse.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapNavigationLayoutWalkRecord.visible(true)
                    binding.mapNavigationLayoutFacilities.visible(false)
                    binding.mapNavigationLayoutCourse.visible(false)
                }
                binding.mapTvFacilities.setOnClickListener {
                    binding.mapViewWalkRecord.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewFacilities.setBackgroundColor(Color.parseColor("#F37520"))
                    binding.mapViewCourse.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvWalkRecord.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvFacilities.setTextColor(Color.parseColor("#F37520"))
                    binding.mapTvCourse.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapNavigationLayoutWalkRecord.visible(false)
                    binding.mapNavigationLayoutFacilities.visible(true)
                    binding.mapNavigationLayoutCourse.visible(false)
                    binding.mapRVFacilities.also { _mapRVFacilities ->
                        arrayListMapFacilities.add(
                            MapFacilities(
                                com.mapo.walkaholic.R.drawable.selector_map_toilet,
                                "화장실",
                                1
                            )
                        )
                        arrayListMapFacilities.add(
                            MapFacilities(
                                com.mapo.walkaholic.R.drawable.selector_map_store,
                                "편의점",
                                2
                            )
                        )
                        arrayListMapFacilities.add(
                            MapFacilities(
                                com.mapo.walkaholic.R.drawable.selector_map_cafe,
                                "까페",
                                3
                            )
                        )
                        arrayListMapFacilities.add(
                            MapFacilities(
                                com.mapo.walkaholic.R.drawable.selector_map_pharmacy,
                                "약국",
                                4
                            )
                        )
                        arrayListMapFacilities.add(
                            MapFacilities(
                                com.mapo.walkaholic.R.drawable.selector_map_bicycle,
                                "따릉이",
                                5
                            )
                        )
                        arrayListMapFacilities.add(
                            MapFacilities(
                                com.mapo.walkaholic.R.drawable.selector_map_police,
                                "경찰서",
                                6
                            )
                        )
                        val linearLayoutManager =
                            LinearLayoutManager(requireContext())
                        linearLayoutManager.orientation =
                            LinearLayoutManager.HORIZONTAL
                        _mapRVFacilities.layoutManager = linearLayoutManager
                        _mapRVFacilities.setHasFixedSize(true)
                        _mapRVFacilities.adapter =
                            MapFacilitiesAdapter(arrayListMapFacilities, this)
                    }
                }
                binding.mapTvCourse.setOnClickListener {
                    binding.mapViewWalkRecord.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewFacilities.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewCourse.setBackgroundColor(Color.parseColor("#F37520"))
                    binding.mapTvWalkRecord.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvFacilities.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvCourse.setTextColor(Color.parseColor("#F37520"))
                    binding.mapNavigationLayoutWalkRecord.visible(false)
                    binding.mapNavigationLayoutFacilities.visible(false)
                    binding.mapNavigationLayoutCourse.visible(true)
                }
                binding.mapIvWalkStart.setOnClickListener {
                    when (walkProcess) {
                        // Stop
                        0 -> {
                            // 정지 상태에서 플레이 버튼 클릭 시
                            sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
                            stepCountSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                            if (sensorManager != null && stepCountSensor != null) {
                                sensorManager!!.registerListener(
                                    this,
                                    stepCountSensor,
                                    SensorManager.SENSOR_DELAY_GAME
                                )
                                binding.mapWalkControllerLayout.visible(false)
                                binding.mapWalkControllerLayout2.visible(true)
                                walkProcess = 1
                                timerTask = kotlin.concurrent.timer(period = 1000) {
                                    time++
                                    val lTime = time.toFloat().toLong()
                                    val hour = TimeUnit.SECONDS.toHours(lTime) - (TimeUnit.SECONDS.toDays(lTime).toInt()) * 24
                                    val minute = TimeUnit.SECONDS.toMinutes(lTime) - (TimeUnit.SECONDS.toHours(lTime)) * 60
                                    val second = TimeUnit.SECONDS.toSeconds(lTime) - (TimeUnit.SECONDS.toMinutes(lTime)) * 60
                                    val zeroFillHour = if(hour < 10) { "0" } else { "" }
                                    val zeroFillMinute = if(minute < 10) { "0" } else { "" }
                                    val zeroFillSecond = if(second < 10) { "0" } else { "" }

                                    requireActivity().runOnUiThread {
                                        binding.walkTime = "${zeroFillHour}${hour.toString().format("%02d", 2)}:${zeroFillMinute}${minute.toString().format("%02d", 2)}:${zeroFillSecond}${second.toString().format("%02d", 2)}"
                                    }
                                }
                            } else {
                                confirmDialog("걸음수 측정을 위한 센서를 찾을 수 없습니다", null, null)
                            }
                        }
                        else -> {
                            // Never Occur
                        }
                    }
                }
                binding.mapIvWalkPause.setOnClickListener {
                    when (walkProcess) {
                        // Stop
                        0 -> {
                            // Never Occur
                        }
                        // Playing
                        1 -> {
                            // 플레이 상태에서 일시 정지 버튼 클릭 시
                            if (sensorManager != null) {
                                binding.mapWalkControllerLayout.visible(false)
                                binding.mapWalkControllerLayout2.visible(true)
                                binding.mapIvWalkPause.setImageResource(com.mapo.walkaholic.R.drawable.ic_walk_start)
                                walkProcess = 2
                                mPauseSteps = mSteps
                                sensorManager!!.unregisterListener(this)
                                timerTask?.cancel()
                            } else {
                                confirmDialog("걸음수 측정을 위한 센서를 찾을 수 없습니다", null, null)
                            }
                        }
                        // Pause
                        2 -> {
                            // 일시 정지 상태에서 일시 정지 버튼 클릭 시
                            if (sensorManager != null && stepCountSensor != null) {
                                binding.mapWalkControllerLayout.visible(false)
                                binding.mapWalkControllerLayout2.visible(true)
                                binding.mapIvWalkPause.setImageResource(com.mapo.walkaholic.R.drawable.ic_walk_pause)
                                walkProcess = 1
                                sensorManager!!.registerListener(
                                    this,
                                    stepCountSensor,
                                    SensorManager.SENSOR_DELAY_GAME
                                )
                                mSteps = mPauseSteps
                                mPauseSteps = 0
                                timerTask = kotlin.concurrent.timer(period = 1000) {
                                    time++
                                    val hour = time / 36000
                                    val minute = time / 6000
                                    val second = time / 100
                                    requireActivity().runOnUiThread {
                                        binding.walkTime = "${"%02d".format(hour)}:${"%02d".format(minute)}:${"%02d".format(second)}"
                                    }
                                }
                            } else {
                                confirmDialog("걸음수 측정을 위한 센서를 찾을 수 없습니다", null, null)
                            }
                        }
                        else -> {
                            // Never Occur
                        }
                    }
                }
                binding.mapIvWalkStop.setOnClickListener {
                    when (walkProcess) {
                        // Stop
                        0 -> {
                            // Never Occur
                        }
                        // Playing
                        1 -> {
                            // 플레이 상태에서 정지 버튼 클릭 시
                            if (sensorManager != null) {
                                binding.mapWalkControllerLayout.visible(true)
                                binding.mapWalkControllerLayout2.visible(false)
                                walkProcess = 0
                                binding.walkNum = "0"
                                val resultWalkNum = mSteps
                                mSteps = 0
                                mPauseSteps = 0
                                mPrevSteps = 0
                                sensorManager!!.unregisterListener(this)
                                timerTask?.cancel()
                                time = 0
                                binding.walkTime = "00:00:00"
                                viewModel.getUser()
                                viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
                                    when(_userResponse) {
                                        is Resource.Success -> {
                                            when(_userResponse.value.code) {
                                                "200" -> {
                                                    viewModel.setReward(_userResponse.value.data.first().id, mSteps)
                                                    viewModel.walkRewardResponse.observe(viewLifecycleOwner, Observer { _walkRewardResponse ->
                                                        when(_walkRewardResponse) {
                                                            is Resource.Success -> {
                                                                when(_walkRewardResponse.value.code) {
                                                                    "200" -> {
                                                                        confirmDialog(_walkRewardResponse.value.message, null, null)
                                                                    }
                                                                    "400" -> {
                                                                        // Error
                                                                    }
                                                                }
                                                            }
                                                            is Resource.Loading -> {
                                                                // Loading
                                                            }
                                                            is Resource.Failure -> {
                                                                handleApiError(_walkRewardResponse) { viewModel.setReward(_userResponse.value.data.first().id, mSteps) }
                                                            }
                                                        }
                                                    })
                                                }
                                                else -> {
                                                    // Error
                                                }
                                            }
                                        }
                                        is Resource.Loading -> {
                                            //
                                        }
                                        is Resource.Failure -> {
                                            handleApiError(_userResponse) { viewModel.getUser() }
                                        }
                                    }
                                })
                            } else {
                                confirmDialog("걸음수 측정을 위한 센서를 찾을 수 없습니다", null, null)
                            }
                        }
                        // Pause
                        2 -> {
                            // 일시 정지 상태에서 정지 버튼 클릭 시
                            if (sensorManager != null) {
                                binding.mapWalkControllerLayout.visible(true)
                                binding.mapWalkControllerLayout2.visible(false)
                                binding.mapIvWalkPause.setImageResource(com.mapo.walkaholic.R.drawable.ic_walk_pause)
                                walkProcess = 0
                                binding.walkNum = "0"
                                mSteps = 0
                                mPauseSteps = 0
                                mPrevSteps = 0
                                sensorManager!!.unregisterListener(this)
                                timerTask?.cancel()
                                time = 0
                                binding.walkTime = "00:00:00"
                            } else {
                                confirmDialog("걸음수 측정을 위한 센서를 찾을 수 없습니다", null, null)
                            }
                        }
                        else -> {
                            // Never Occur
                        }
                    }
                }
            }
            false -> {
                binding.mapWalkControllerLayout.visible(false)
                binding.mapWalkControllerLayout2.visible(false)
                binding.mapThemeCourseLayout.visible(true)
                binding.mapNavigationLayout.visible(false)
                binding.mapBtnFavoriteCourse.setOnClickListener {
                    alertDialog("코스 정보를 찜 하시겠습니까?", null, {
                        // Favorite Rest
                        // If success...
                        confirmDialog("찜 목록에 추가 되었습니다!", null, null)
                    })
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) {
                mMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        timerTask?.cancel()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLocationChanged(location: Location) {
        if (location == null) {
            return
        }

        mMap?.let {
            val currentLatLng = LatLng(location)
            GlobalApplication.currentLng = currentLatLng.latitude.toString()
            GlobalApplication.currentLat = currentLatLng.longitude.toString()

            val locationOverlay = it.locationOverlay
            locationOverlay.isVisible = true
            locationOverlay.position = currentLatLng
            locationOverlay.bearing = location.bearing

            it.moveCamera(CameraUpdate.scrollTo(currentLatLng))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.mMap = naverMap
        mMap.locationSource = locationSource
        when (mapArgs.isWalk) {
            true -> {
                mMap.moveCamera(
                    CameraUpdate.toCameraPosition(
                        CameraPosition(
                            LatLng(
                                GlobalApplication.currentLat.toDouble(),
                                GlobalApplication.currentLng.toDouble()
                            ),
                            12.0
                        )
                    )
                )
                mMap.locationTrackingMode = LocationTrackingMode.Follow
            }
            false -> {
                mMap.locationTrackingMode = LocationTrackingMode.NoFollow
                viewModel.getThemeCourse(mapArgs.themeId)
                viewModel.themeCourseResponse.observe(
                    viewLifecycleOwner,
                    Observer { _themeCourseResponse ->
                        when (_themeCourseResponse) {
                            is Resource.Success -> {
                                when (_themeCourseResponse.value.code) {
                                    "200" -> {
                                        binding.themeCourse = _themeCourseResponse.value.data
                                    }
                                    else -> {
                                        binding.themeCourse = ThemeCourseResponse.DataThemeCourse(
                                            -1,
                                            "오류",
                                            "오류",
                                            "오류",
                                            "오류",
                                            "오류",
                                            "오류",
                                            "오류",
                                            "오류"
                                        )
                                        // Error
                                        confirmDialog(
                                            _themeCourseResponse.value.message,
                                            {
                                                viewModel.getThemeCourse(mapArgs.themeId)
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
                                binding.themeCourse = ThemeCourseResponse.DataThemeCourse(
                                    -1,
                                    "오류",
                                    "오류",
                                    "오류",
                                    "오류",
                                    "오류",
                                    "오류",
                                    "오류",
                                    "오류"
                                )
                                // Network Error
                                handleApiError(_themeCourseResponse) {
                                    viewModel.getThemeCourse(
                                        mapArgs.themeId
                                    )
                                }
                            }
                        }
                    })
                viewModel.getThemeCourseRoute(mapArgs.themeId)
                viewModel.themeCourseRouteResponse.observe(
                    viewLifecycleOwner,
                    Observer { _themeCourseRouteResponse ->
                        when (_themeCourseRouteResponse) {
                            is Resource.Success -> {
                                when (_themeCourseRouteResponse.value.code) {
                                    "200" -> {
                                        val arrayListLatLng = mutableListOf<LatLng>()
                                        _themeCourseRouteResponse.value.data.forEachIndexed { _courseRouteIndex, _courseRouteElement ->
                                            if (_courseRouteIndex == 0) {
                                                mMap.moveCamera(
                                                    CameraUpdate.scrollTo(
                                                        LatLng(
                                                            _courseRouteElement.x.toDouble(),
                                                            _courseRouteElement.y.toDouble()
                                                        )
                                                    )
                                                )
                                            }
                                            arrayListLatLng.add(
                                                LatLng(
                                                    _courseRouteElement.x.toDouble(),
                                                    _courseRouteElement.y.toDouble()
                                                )
                                            )
                                            if (_courseRouteIndex == _themeCourseRouteResponse.value.data.size - 1) {
                                                val path = PathOverlay()
                                                path.coords = arrayListLatLng
                                                path.map = mMap
                                            }
                                        }
                                    }
                                    else -> {
                                        // Error
                                        confirmDialog(
                                            _themeCourseRouteResponse.value.message,
                                            {
                                                viewModel.getThemeCourseRoute(mapArgs.themeId)
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
                                handleApiError(_themeCourseRouteResponse) {
                                    viewModel.getThemeCourseRoute(
                                        mapArgs.themeId
                                    )
                                }
                            }
                        }
                    })
            }
        }
        mMap.uiSettings.isLocationButtonEnabled = true
        mMap.uiSettings.logoGravity = Gravity.TOP + Gravity.RIGHT
        mMap.uiSettings.setLogoMargin(80, 80, 80, 80)
        mMap.minZoom = 5.0
        mMap.maxZoom = 18.0
        mMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
        mMap.addOnCameraIdleListener { }
        mMap.addOnCameraChangeListener { reason, animated -> }
        mMap.setOnMapClickListener { point, coord ->
            when (mapArgs.isWalk) {
                true -> {
                    when (binding.mapNavigationLayout.visibility) {
                        View.VISIBLE -> {
                            binding.mapNavigationLayout.visible(false)
                        }
                        View.GONE -> {
                            binding.mapNavigationLayout.visible(true)
                        }
                        else -> {
                            // Never Occur
                        }
                    }
                    when (walkProcess) {
                        // 정지 상태
                        0 -> {
                            when (binding.mapWalkControllerLayout.visibility) {
                                View.VISIBLE -> {
                                    binding.mapWalkControllerLayout.visible(false)
                                }
                                View.GONE -> {
                                    binding.mapWalkControllerLayout.visible(true)
                                }
                                else -> {
                                    // Never Occur
                                }
                            }
                        }
                        // 플레이 상태
                        1 -> {
                            when (binding.mapWalkControllerLayout2.visibility) {
                                View.VISIBLE -> {
                                    binding.mapWalkControllerLayout2.visible(false)
                                }
                                View.GONE -> {
                                    binding.mapWalkControllerLayout2.visible(true)
                                }
                                else -> {
                                    // Never Occur
                                }
                            }
                        }
                        // 일시 정지 상태
                        2 -> {
                            when (binding.mapWalkControllerLayout2.visibility) {
                                View.VISIBLE -> {
                                    binding.mapWalkControllerLayout2.visible(false)
                                }
                                View.GONE -> {
                                    binding.mapWalkControllerLayout2.visible(true)
                                }
                                else -> {
                                    // Never Occur
                                }
                            }
                        }
                    }
                }
                false -> {
                    binding.mapNavigationLayout.visible(false)
                    when (binding.mapThemeCourseLayout.visibility) {
                        View.VISIBLE -> {
                            binding.mapThemeCourseLayout.visible(false)
                        }
                        View.GONE -> {
                            binding.mapThemeCourseLayout.visible(true)
                        }
                        else -> {
                            // Never Occur
                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mapArgs.isWalk) {
                    confirmDialog(getString(com.mapo.walkaholic.R.string.err_deny_prev), null, null)
                } else {
                    val navDirection: NavDirections? =
                        MapFragmentDirections.actionActionBnvMapToActionBnvTheme()
                    if (navDirection != null) {
                        findNavController().navigate(navDirection)
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun getViewModel() = MapViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMapBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }

    override fun onItemClick(position: Int, facilitiesId: Int, isSelected: Boolean) {
        when (facilitiesId) {
            1 -> {
                if (!isSelected) {
                    viewModel.getMarkerToilet(
                        facilitiesId,
                        GlobalApplication.currentLng,
                        GlobalApplication.currentLat
                    )
                    viewModel.markerToiletResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerToiletResponse ->
                            when (_markerToiletResponse) {
                                is Resource.Success -> {
                                    when (_markerToiletResponse.value.code) {
                                        "200" -> {
                                            _markerToiletResponse.value.data.forEachIndexed { _toiletIndex, _toiletElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _toiletElement.name + "\n" + _toiletElement.address
                                                    }
                                                }
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _toiletElement.y.toDouble(),
                                                    _toiletElement.x.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_toilet)
                                                marker.width = Marker.SIZE_AUTO
                                                marker.height = Marker.SIZE_AUTO
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                infoWindow.isVisible = false
                                                marker.setOnClickListener {
                                                    if (infoWindow.isVisible) {
                                                        infoWindow.isVisible = false
                                                        infoWindow.close()
                                                    } else {
                                                        infoWindow.isVisible = true
                                                        infoWindow.open(marker)
                                                    }
                                                    true
                                                }
                                                arrayListToiletMarker.add(marker)
                                                marker.map = mMap
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _markerToiletResponse.value.message,
                                                {
                                                    viewModel.getMarkerToilet(
                                                        facilitiesId,
                                                        GlobalApplication.currentLat,
                                                        GlobalApplication.currentLng
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    handleApiError(_markerToiletResponse) {
                                        viewModel.getMarkerToilet(
                                            facilitiesId,
                                            GlobalApplication.currentLat,
                                            GlobalApplication.currentLng
                                        )
                                    }
                                }
                            }
                        })
                } else {
                    arrayListToiletMarker.forEachIndexed { _markerIndex, _markerElement ->
                        _markerElement.map = null
                    }
                }
            }
            2 -> {
                if (!isSelected) {
                    viewModel.getMarkerStore(
                        facilitiesId,
                        GlobalApplication.currentLng,
                        GlobalApplication.currentLat
                    )
                    viewModel.markerStoreResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerStoreResponse ->
                            when (_markerStoreResponse) {
                                is Resource.Success -> {
                                    when (_markerStoreResponse.value.code) {
                                        "200" -> {
                                            _markerStoreResponse.value.data.forEachIndexed { _StoreIndex, _StoreElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _StoreElement.name + "\n" + _StoreElement.address
                                                    }
                                                }
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _StoreElement.y.toDouble(),
                                                    _StoreElement.x.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_store)
                                                marker.width = Marker.SIZE_AUTO
                                                marker.height = Marker.SIZE_AUTO
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                infoWindow.isVisible = false
                                                marker.setOnClickListener {
                                                    if (infoWindow.isVisible) {
                                                        infoWindow.isVisible = false
                                                        infoWindow.close()
                                                    } else {
                                                        infoWindow.isVisible = true
                                                        infoWindow.open(marker)
                                                    }
                                                    true
                                                }
                                                arrayListStoreMarker.add(marker)
                                                marker.map = mMap
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _markerStoreResponse.value.message,
                                                {
                                                    viewModel.getMarkerStore(
                                                        facilitiesId,
                                                        GlobalApplication.currentLat,
                                                        GlobalApplication.currentLng
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    handleApiError(_markerStoreResponse) {
                                        viewModel.getMarkerStore(
                                            facilitiesId,
                                            GlobalApplication.currentLat,
                                            GlobalApplication.currentLng
                                        )
                                    }
                                }
                            }
                        })
                } else {
                    arrayListStoreMarker.forEachIndexed { _markerIndex, _markerElement ->
                        _markerElement.map = null
                    }
                }
            }
            3 -> {
                if (!isSelected) {
                    viewModel.getMarkerCafe(
                        facilitiesId,
                        GlobalApplication.currentLng,
                        GlobalApplication.currentLat
                    )
                    viewModel.markerCafeResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerCafeResponse ->
                            when (_markerCafeResponse) {
                                is Resource.Success -> {
                                    when (_markerCafeResponse.value.code) {
                                        "200" -> {
                                            _markerCafeResponse.value.data.forEachIndexed { _CafeIndex, _CafeElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _CafeElement.name + "\n" + _CafeElement.address
                                                    }
                                                }
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _CafeElement.y.toDouble(),
                                                    _CafeElement.x.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_cafe)
                                                marker.width = Marker.SIZE_AUTO
                                                marker.height = Marker.SIZE_AUTO
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                infoWindow.isVisible = false
                                                marker.setOnClickListener {
                                                    if (infoWindow.isVisible) {
                                                        infoWindow.isVisible = false
                                                        infoWindow.close()
                                                    } else {
                                                        infoWindow.isVisible = true
                                                        infoWindow.open(marker)
                                                    }
                                                    true
                                                }
                                                arrayListCafeMarker.add(marker)
                                                marker.map = mMap
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _markerCafeResponse.value.message,
                                                {
                                                    viewModel.getMarkerCafe(
                                                        facilitiesId,
                                                        GlobalApplication.currentLat,
                                                        GlobalApplication.currentLng
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    handleApiError(_markerCafeResponse) {
                                        viewModel.getMarkerCafe(
                                            facilitiesId,
                                            GlobalApplication.currentLat,
                                            GlobalApplication.currentLng
                                        )
                                    }
                                }
                            }
                        })
                } else {
                    arrayListCafeMarker.forEachIndexed { _markerIndex, _markerElement ->
                        _markerElement.map = null
                    }
                }
            }
            4 -> {
                if (!isSelected) {
                    viewModel.getMarkerPharmacy(
                        facilitiesId,
                        GlobalApplication.currentLng,
                        GlobalApplication.currentLat
                    )
                    viewModel.markerPharmacyResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerPharmacyResponse ->
                            when (_markerPharmacyResponse) {
                                is Resource.Success -> {
                                    when (_markerPharmacyResponse.value.code) {
                                        "200" -> {
                                            _markerPharmacyResponse.value.data.forEachIndexed { _PharmacyIndex, _PharmacyElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _PharmacyElement.name + "\n" + _PharmacyElement.address
                                                    }
                                                }
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _PharmacyElement.y.toDouble(),
                                                    _PharmacyElement.x.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_pharmacy)
                                                marker.width = Marker.SIZE_AUTO
                                                marker.height = Marker.SIZE_AUTO
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                infoWindow.isVisible = false
                                                marker.setOnClickListener {
                                                    if (infoWindow.isVisible) {
                                                        infoWindow.isVisible = false
                                                        infoWindow.close()
                                                    } else {
                                                        infoWindow.isVisible = true
                                                        infoWindow.open(marker)
                                                    }
                                                    true
                                                }
                                                arrayListPharmacyMarker.add(marker)
                                                marker.map = mMap
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _markerPharmacyResponse.value.message,
                                                {
                                                    viewModel.getMarkerPharmacy(
                                                        facilitiesId,
                                                        GlobalApplication.currentLat,
                                                        GlobalApplication.currentLng
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    handleApiError(_markerPharmacyResponse) {
                                        viewModel.getMarkerPharmacy(
                                            facilitiesId,
                                            GlobalApplication.currentLat,
                                            GlobalApplication.currentLng
                                        )
                                    }
                                }
                            }
                        })
                } else {
                    arrayListPharmacyMarker.forEachIndexed { _markerIndex, _markerElement ->
                        _markerElement.map = null
                    }
                }
            }
            5 -> {
                if (!isSelected) {
                    viewModel.getMarkerBicycle(
                        facilitiesId,
                        GlobalApplication.currentLng,
                        GlobalApplication.currentLat
                    )
                    viewModel.markerBicycleResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerBicycleResponse ->
                            when (_markerBicycleResponse) {
                                is Resource.Success -> {
                                    when (_markerBicycleResponse.value.code) {
                                        "200" -> {
                                            _markerBicycleResponse.value.data.forEachIndexed { _BicycleIndex, _BicycleElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _BicycleElement.name + "\n" + _BicycleElement.address
                                                    }
                                                }
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _BicycleElement.y.toDouble(),
                                                    _BicycleElement.x.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_bicycle)
                                                marker.width = Marker.SIZE_AUTO
                                                marker.height = Marker.SIZE_AUTO
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                infoWindow.isVisible = false
                                                marker.setOnClickListener {
                                                    if (infoWindow.isVisible) {
                                                        infoWindow.isVisible = false
                                                        infoWindow.close()
                                                    } else {
                                                        infoWindow.isVisible = true
                                                        infoWindow.open(marker)
                                                    }
                                                    true
                                                }
                                                arrayListBicycleMarker.add(marker)
                                                marker.map = mMap
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _markerBicycleResponse.value.message,
                                                {
                                                    viewModel.getMarkerBicycle(
                                                        facilitiesId,
                                                        GlobalApplication.currentLat,
                                                        GlobalApplication.currentLng
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    handleApiError(_markerBicycleResponse) {
                                        viewModel.getMarkerBicycle(
                                            facilitiesId,
                                            GlobalApplication.currentLat,
                                            GlobalApplication.currentLng
                                        )
                                    }
                                }
                            }
                        })
                } else {
                    arrayListBicycleMarker.forEachIndexed { _markerIndex, _markerElement ->
                        _markerElement.map = null
                    }
                }
            }
            6 -> {
                if (!isSelected) {
                    viewModel.getMarkerPolice(
                        facilitiesId,
                        GlobalApplication.currentLng,
                        GlobalApplication.currentLat
                    )
                    viewModel.markerPoliceResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerPoliceResponse ->
                            when (_markerPoliceResponse) {
                                is Resource.Success -> {
                                    when (_markerPoliceResponse.value.code) {
                                        "200" -> {
                                            Log.d(
                                                TAG,
                                                GlobalApplication.currentLat + GlobalApplication.currentLng
                                            )
                                            Log.d(TAG, _markerPoliceResponse.value.data.toString())
                                            _markerPoliceResponse.value.data.forEachIndexed { _policeIndex, _policeElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _policeElement.name + "\n" + _policeElement.address
                                                    }
                                                }
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _policeElement.y.toDouble(),
                                                    _policeElement.x.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_police)
                                                marker.width = Marker.SIZE_AUTO
                                                marker.height = Marker.SIZE_AUTO
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                infoWindow.isVisible = false
                                                marker.setOnClickListener {
                                                    if (infoWindow.isVisible) {
                                                        infoWindow.isVisible = false
                                                        infoWindow.close()
                                                    } else {
                                                        infoWindow.isVisible = true
                                                        infoWindow.open(marker)
                                                    }
                                                    true
                                                }
                                                arrayListPoliceMarker.add(marker)
                                                marker.map = mMap
                                            }
                                        }
                                        else -> {
                                            confirmDialog(
                                                _markerPoliceResponse.value.message,
                                                {
                                                    viewModel.getMarkerPolice(
                                                        facilitiesId,
                                                        GlobalApplication.currentLat,
                                                        GlobalApplication.currentLng
                                                    )
                                                },
                                                "재시도"
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Failure -> {
                                    handleApiError(_markerPoliceResponse) {
                                        viewModel.getMarkerPolice(
                                            facilitiesId,
                                            GlobalApplication.currentLat,
                                            GlobalApplication.currentLng
                                        )
                                    }
                                }
                            }
                        })
                } else {
                    arrayListPoliceMarker.forEachIndexed { _markerIndex, _markerElement ->
                        _markerElement.map = null
                    }
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (mPrevSteps < 1) {
                    mPrevSteps = event.values[0].toInt()
                }
                mSteps = (event.values[0] - mPrevSteps).toInt()
                binding.walkNum = mSteps.toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}