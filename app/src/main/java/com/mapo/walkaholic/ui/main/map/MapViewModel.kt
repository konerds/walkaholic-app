package com.mapo.walkaholic.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.Point
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.MapResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.launch

class MapViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    private var mMap: NaverMap? = null
    private val markers = MutableLiveData<List<Marker>>()
    val points = MutableLiveData<List<Point>>()
    override fun init() {}

    fun init(mMap: NaverMap) {
        this.mMap = mMap
    }

    private val _mapResponse: MutableLiveData<Resource<MapResponse>> = MutableLiveData()
    val mapResponse: LiveData<Resource<MapResponse>>
        get() = _mapResponse

    private val tempMarkerList = ArrayList<Marker>()

    fun getMarkers(zoom: Double?): LiveData<List<Marker>> {
        setMarkers(zoom)
        return this.markers
    }

    fun setMarkers(zoom: Double?) {
        for (it in tempMarkerList) {
            it.map = null
        }
        tempMarkerList.clear()

        fetchList(tempMarkerList, markers, zoom)
    }

    private fun fetchList(
        markers: ArrayList<Marker>,
        liveDataMarkers: MutableLiveData<List<Marker>>?,
        zoom: Double?
    ) = viewModelScope.launch {
        _mapResponse.value = mainRepository.getPoints(
            MapRequestBody(

                // 카메라 경도, 위도
                mMap!!.cameraPosition.target.longitude,
                mMap!!.cameraPosition.target.latitude,

                mMap!!.contentBounds.southWest.longitude, // 뷰포인트 남서
                mMap!!.contentBounds.southWest.latitude,

                mMap!!.contentBounds.southEast.longitude, // 뷰포인트 남동
                mMap!!.contentBounds.southEast.latitude,

                mMap!!.contentBounds.northEast.longitude, // 뷰포인트 북동
                mMap!!.contentBounds.northEast.latitude,

                mMap!!.contentBounds.northWest.longitude, // 뷰포인트 북서
                mMap!!.contentBounds.northWest.latitude
            )
        )
    }

    fun getPoints() : LiveData<List<Point>> {
        return this.points
    }
}