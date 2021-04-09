package com.mapo.walkaholic.ui.service

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.repository.MapRepository
import com.mapo.walkaholic.databinding.FragmentMapBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MapRepository>(), OnMapReadyCallback, LocationListener {
    private lateinit var mapView: MapView
    private lateinit var mMap: NaverMap
    private lateinit var locationManager: LocationManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        NaverMapSdk.getInstance(GlobalApplication.getGlobalApplicationContext()).client = NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))
        binding.mapView.getMapAsync(this)
        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasPermission()) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this)
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
        if (hasPermission()) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 10f, this)
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                        it, PERMISSIONS, PERMISSION_REQUEST_CODE)
            }
        }
        mapView.onStart()
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
        mMap = naverMap
        viewModel.init(mMap)

        mMap.addOnCameraIdleListener {
            setupDataOnMap(mMap)
        }

        mMap.addOnCameraChangeListener { reason, animated ->
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
            MapRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)

    private fun hasPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(requireContext(), PERMISSIONS[0]) ==
                PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(requireContext(), PERMISSIONS[1]) ==
                PermissionChecker.PERMISSION_GRANTED
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private val PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}