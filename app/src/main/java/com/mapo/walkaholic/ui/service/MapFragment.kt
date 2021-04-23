package com.mapo.walkaholic.ui.service

import android.content.ContentValues
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mapo.walkaholic.data.network.Api
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
        Log.i(
            ContentValues.TAG, "최근 위치 ${locationSource.lastLocation}"
        )
        binding.mapView.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                        grantResults)) {
            if (!locationSource.isActivated) {
                //mMap.locationTrackingMode = LocationTrackingMode.None
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
        //naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(LatLng(35.231574, 129.084433), 12.0)))
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.uiSettings.logoGravity = Gravity.TOP + Gravity.RIGHT
        naverMap.uiSettings.setLogoMargin(80, 80, 80, 80)
        naverMap.minZoom = 5.0
        naverMap.maxZoom = 18.0
        //naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
        mMap = naverMap
        viewModel.init(mMap)
        mMap.addOnCameraIdleListener {
            setupDataOnMap(mMap)
        }
        mMap.addOnCameraChangeListener { reason, animated ->
        }
        mMap.setOnMapClickListener { point, coord ->
            Log.i(
                    ContentValues.TAG, "처리 결과 : ${coord.longitude}, ${coord.latitude}}"
            )
            Toast.makeText(requireContext(), "${coord.longitude}, ${coord.latitude}",
                    Toast.LENGTH_SHORT).show()
            GlobalApplication.currentLng = coord.longitude.toString()
            GlobalApplication.currentLat = coord.latitude.toString()
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
            MapRepository(remoteDataSource.buildRetrofitApi(Api::class.java))

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}