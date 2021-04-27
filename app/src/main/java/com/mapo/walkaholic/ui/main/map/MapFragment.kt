package com.mapo.walkaholic.ui.main.map

import android.content.ContentValues
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.repository.MapRepository
import com.mapo.walkaholic.databinding.FragmentMapBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MapRepository>(), OnMapReadyCallback, LocationListener {
    private lateinit var mapView: MapView
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mMap: NaverMap

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        binding.mapView.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                        grantResults)) {
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

            GlobalApplication.currentLng = coord.longitude.toString()
            GlobalApplication.currentLat = coord.latitude.toString()

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
        //naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(LatLng(35.231574, 129.084433), 12.0)))
        mMap.locationTrackingMode = LocationTrackingMode.Follow
        mMap.uiSettings.isLocationButtonEnabled = true
        mMap.uiSettings.logoGravity = Gravity.TOP + Gravity.RIGHT
        mMap.uiSettings.setLogoMargin(80, 80, 80, 80)
        mMap.minZoom = 5.0
        mMap.maxZoom = 18.0
        //naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
        viewModel.init(mMap)
        mMap.addOnCameraIdleListener {
            setupDataOnMap(mMap)
        }
        mMap.addOnCameraChangeListener { reason, animated ->
        }
        mMap.setOnMapClickListener { point, coord ->
            Toast.makeText(requireContext(), "${coord.longitude}, ${coord.latitude}",
                    Toast.LENGTH_SHORT).show()
        }
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

    override fun getViewModel() = MapViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentMapBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            MapRepository(remoteDataSource.buildRetrofitApi(InnerApi::class.java), userPreferences)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}