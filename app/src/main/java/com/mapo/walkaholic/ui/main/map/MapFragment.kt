package com.mapo.walkaholic.ui.main.map

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.LocationListener
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
import com.mapo.walkaholic.ui.main.dashboard.character.shop.DashboardCharacterShopFragmentDirections
import com.mapo.walkaholic.ui.visible
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MainRepository>(),
    OnMapReadyCallback, LocationListener {
    private lateinit var mapView: MapView
    private lateinit var mMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    val mapArgs: MapFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
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

        binding.mapView.getMapAsync(this)
        when (mapArgs.isWalk) {
            true -> {
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
                    /*binding.mapTvWalkRecord.isSelected = true
                    binding.mapTvFacilities.isSelected = false
                    binding.mapTvCourse.isSelected = false
                    binding.mapViewWalkRecord.isSelected = true
                    binding.mapViewFacilities.isSelected = false
                    binding.mapViewCourse.isSelected = false*/
                    binding.mapNavigationLayoutWalkRecord.visible(true)
                    binding.mapNavigationLayoutCourse.visible(false)
                    binding.mapNavigationLayoutFacilities.visible(false)
                }
                binding.mapTvFacilities.setOnClickListener {
                    binding.mapViewWalkRecord.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewFacilities.setBackgroundColor(Color.parseColor("#F37520"))
                    binding.mapViewCourse.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvWalkRecord.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvFacilities.setTextColor(Color.parseColor("#F37520"))
                    binding.mapTvCourse.setTextColor(Color.parseColor("#E3E0DB"))
                    /*binding.mapTvWalkRecord.isSelected = false
                    binding.mapTvFacilities.isSelected = true
                    binding.mapTvCourse.isSelected = false
                    binding.mapViewWalkRecord.isSelected = false
                    binding.mapViewFacilities.isSelected = true
                    binding.mapViewCourse.isSelected = false*/
                    binding.mapNavigationLayoutWalkRecord.visible(false)
                    binding.mapNavigationLayoutCourse.visible(true)
                    binding.mapNavigationLayoutFacilities.visible(false)
                }
                binding.mapTvCourse.setOnClickListener {
                    binding.mapViewWalkRecord.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewFacilities.setBackgroundColor(Color.parseColor("#E3E0DB"))
                    binding.mapViewCourse.setBackgroundColor(Color.parseColor("#F37520"))
                    binding.mapTvWalkRecord.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvFacilities.setTextColor(Color.parseColor("#E3E0DB"))
                    binding.mapTvCourse.setTextColor(Color.parseColor("#F37520"))
                    /*binding.mapTvWalkRecord.isSelected = false
                    binding.mapTvFacilities.isSelected = false
                    binding.mapTvCourse.isSelected = true
                    binding.mapViewWalkRecord.isSelected = false
                    binding.mapViewFacilities.isSelected = false
                    binding.mapViewCourse.isSelected = true*/
                    binding.mapNavigationLayoutWalkRecord.visible(false)
                    binding.mapNavigationLayoutCourse.visible(false)
                    binding.mapNavigationLayoutFacilities.visible(true)
                }
            }
            false -> {
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
        /*val currentLocationSource = GlobalApplication.getLocationSource()
        if (currentLocationSource != null) {
            if (currentLocationSource.onRequestPermissionsResult(
                    requestCode, permissions,
                    grantResults
                )
            ) {
                if (!currentLocationSource.isActivated) {
                    mMap.locationTrackingMode = LocationTrackingMode.None
                }
                return
            }
        }*/
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
            val coord = LatLng(location)

            GlobalApplication.currentLng = coord.latitude.toString()
            GlobalApplication.currentLat = coord.longitude.toString()
            Log.e("현재 위치 : ", "${coord.longitude}, ${coord.latitude}")

            val locationOverlay = it.locationOverlay
            locationOverlay.isVisible = true
            locationOverlay.position = coord
            locationOverlay.bearing = location.bearing

            it.moveCamera(CameraUpdate.scrollTo(coord))
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
        mMap.moveCamera(
            CameraUpdate.toCameraPosition(
                CameraPosition(
                    LatLng(35.231574, 129.084433),
                    12.0
                )
            )
        )
        mMap.locationTrackingMode = LocationTrackingMode.Follow
        mMap.uiSettings.isLocationButtonEnabled = true
        mMap.uiSettings.logoGravity = Gravity.TOP + Gravity.RIGHT
        mMap.uiSettings.setLogoMargin(80, 80, 80, 80)
        mMap.minZoom = 5.0
        mMap.maxZoom = 18.0
        mMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
        /*mMap.addOnCameraIdleListener {
            setupDataOnMap(mMap)
        }*/
        mMap.addOnCameraChangeListener { reason, animated ->
        }
        mMap.setOnMapClickListener { point, coord ->
            Toast.makeText(
                requireContext(), "${coord.longitude}, ${coord.latitude}",
                Toast.LENGTH_SHORT
            ).show()
        }
        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = mMap
    }

    private fun setupDataOnMap(naverMap: NaverMap) {
        val zoom = naverMap.cameraPosition.zoom
        viewModel.getMarkers(zoom).observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                for (i in it) {
                    i.map = naverMap
                }
            } else {
                this.onResume()
            }
        })
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
}