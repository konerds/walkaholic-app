package com.mapo.walkaholic.ui.main.map

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
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
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MainRepository>(),
    OnMapReadyCallback, LocationListener, MapFacilitiesClickListener {
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
                        val arrayListMapFacilities = arrayListOf<MapFacilities>()
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

    override fun onItemClick(position: Int, facilitiesId: Int, isSelected: Boolean) {
        when (facilitiesId) {
            1 -> {
                val arrayListToiletMarker = arrayListOf<Marker>()
                if (!isSelected) {
                    viewModel.getMarkerToilet(
                        facilitiesId,
                        GlobalApplication.currentLat,
                        GlobalApplication.currentLng
                    )
                    viewModel.markerToiletResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerToiletResponse ->
                            when (_markerToiletResponse) {
                                is Resource.Success -> {
                                    when (_markerToiletResponse.value.code) {
                                        "200" -> {
                                            _markerToiletResponse.value.data.forEachIndexed { _ToiletIndex, _ToiletElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _ToiletElement.name + "\n" + _ToiletElement.address
                                                    }
                                                }
                                                infoWindow.alpha = 0.5f
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _ToiletElement.x.toDouble(),
                                                    _ToiletElement.y.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_toilet)
                                                marker.width = 20
                                                marker.height = 20
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                marker.setOnClickListener {
                                                    infoWindow.open(marker)
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
                val arrayListStoreMarker = arrayListOf<Marker>()
                if (!isSelected) {
                    viewModel.getMarkerStore(
                        facilitiesId,
                        GlobalApplication.currentLat,
                        GlobalApplication.currentLng
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
                                                infoWindow.alpha = 0.5f
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _StoreElement.x.toDouble(),
                                                    _StoreElement.y.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_store)
                                                marker.width = 20
                                                marker.height = 20
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                marker.setOnClickListener {
                                                    infoWindow.open(marker)
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
                val arrayListCafeMarker = arrayListOf<Marker>()
                if (!isSelected) {
                    viewModel.getMarkerCafe(
                        facilitiesId,
                        GlobalApplication.currentLat,
                        GlobalApplication.currentLng
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
                                                infoWindow.alpha = 0.5f
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _CafeElement.x.toDouble(),
                                                    _CafeElement.y.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_cafe)
                                                marker.width = 20
                                                marker.height = 20
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                marker.setOnClickListener {
                                                    infoWindow.open(marker)
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
                val arrayListPharmacyMarker = arrayListOf<Marker>()
                if (!isSelected) {
                    viewModel.getMarkerPharmacy(
                        facilitiesId,
                        GlobalApplication.currentLat,
                        GlobalApplication.currentLng
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
                                                infoWindow.alpha = 0.5f
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _PharmacyElement.x.toDouble(),
                                                    _PharmacyElement.y.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_pharmacy)
                                                marker.width = 20
                                                marker.height = 20
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                marker.setOnClickListener {
                                                    infoWindow.open(marker)
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
                val arrayListBicycleMarker = arrayListOf<Marker>()
                if (!isSelected) {
                    viewModel.getMarkerBicycle(
                        facilitiesId,
                        GlobalApplication.currentLat,
                        GlobalApplication.currentLng
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
                                                infoWindow.alpha = 0.5f
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _BicycleElement.x.toDouble(),
                                                    _BicycleElement.y.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_bicycle)
                                                marker.width = 20
                                                marker.height = 20
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                marker.setOnClickListener {
                                                    infoWindow.open(marker)
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
                val arrayListPoliceMarker = arrayListOf<Marker>()
                if (!isSelected) {
                    viewModel.getMarkerPolice(
                        facilitiesId,
                        GlobalApplication.currentLat,
                        GlobalApplication.currentLng
                    )
                    viewModel.markerPoliceResponse.observe(
                        viewLifecycleOwner,
                        Observer { _markerPoliceResponse ->
                            when (_markerPoliceResponse) {
                                is Resource.Success -> {
                                    when (_markerPoliceResponse.value.code) {
                                        "200" -> {
                                            _markerPoliceResponse.value.data.forEachIndexed { _policeIndex, _policeElement ->
                                                val infoWindow = InfoWindow()
                                                infoWindow.adapter = object :
                                                    InfoWindow.DefaultTextAdapter(requireContext()) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return _policeElement.name + "\n" + _policeElement.address
                                                    }
                                                }
                                                infoWindow.alpha = 0.5f
                                                val marker = Marker()
                                                marker.position = LatLng(
                                                    _policeElement.x.toDouble(),
                                                    _policeElement.y.toDouble()
                                                )
                                                marker.icon =
                                                    OverlayImage.fromResource(com.mapo.walkaholic.R.drawable.ic_small_police)
                                                marker.width = 20
                                                marker.height = 20
                                                marker.zIndex = facilitiesId
                                                marker.isHideCollidedSymbols = true
                                                marker.setOnClickListener {
                                                    infoWindow.open(marker)
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
}