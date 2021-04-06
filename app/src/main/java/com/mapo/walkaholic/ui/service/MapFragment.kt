package com.mapo.walkaholic.ui.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.Api
import com.mapo.walkaholic.data.repository.MapRepository
import com.mapo.walkaholic.databinding.FragmentMapBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MapRepository>(), OnMapReadyCallback {
    private lateinit var mMap: NaverMap

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        NaverMapSdk.getInstance(GlobalApplication.getGlobalApplicationContext()).client = NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(@NonNull naverMap: NaverMap) {
        naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(LatLng(35.231574, 129.084433), 12.0)))
        mMap = naverMap
        viewModel.init(mMap)

        mMap.addOnCameraIdleListener{
            setupDataOnMap(mMap)
        }

        mMap.addOnCameraChangeListener { reason, animated ->
        }
    }

    private fun setupDataOnMap(@NonNull naverMap: NaverMap) {
        val zoom = naverMap.cameraPosition.zoom

        viewModel.getMarkers(zoom).observe(this, Observer {
            if(!it.isNullOrEmpty()){
                for(i in it){
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
}