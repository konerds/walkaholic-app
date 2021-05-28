package com.mapo.walkaholic.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.Point
import com.mapo.walkaholic.data.model.request.MapRequestBody
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.launch

class MapViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {}
    private var mMap: NaverMap? = null
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _themeCourseResponse: MutableLiveData<Resource<ThemeCourseResponse>> =
        MutableLiveData()
    val themeCourseResponse: LiveData<Resource<ThemeCourseResponse>>
        get() = _themeCourseResponse
    private val _themeCourseRouteResponse: MutableLiveData<Resource<ThemeCourseRouteResponse>> =
        MutableLiveData()
    val themeCourseRouteResponse: LiveData<Resource<ThemeCourseRouteResponse>>
        get() = _themeCourseRouteResponse
    private val _markerToiletResponse: MutableLiveData<Resource<MarkerLatLngResponse>> =
        MutableLiveData()
    val markerToiletResponse: LiveData<Resource<MarkerLatLngResponse>>
        get() = _markerToiletResponse
    private val _markerStoreResponse: MutableLiveData<Resource<MarkerLatLngResponse>> =
        MutableLiveData()
    val markerStoreResponse: LiveData<Resource<MarkerLatLngResponse>>
        get() = _markerStoreResponse
    private val _markerCafeResponse: MutableLiveData<Resource<MarkerLatLngResponse>> =
        MutableLiveData()
    val markerCafeResponse: LiveData<Resource<MarkerLatLngResponse>>
        get() = _markerCafeResponse
    private val _markerPharmacyResponse: MutableLiveData<Resource<MarkerLatLngResponse>> =
        MutableLiveData()
    val markerPharmacyResponse: LiveData<Resource<MarkerLatLngResponse>>
        get() = _markerPharmacyResponse
    private val _markerBicycleResponse: MutableLiveData<Resource<MarkerLatLngResponse>> =
        MutableLiveData()
    val markerBicycleResponse: LiveData<Resource<MarkerLatLngResponse>>
        get() = _markerBicycleResponse
    private val _markerPoliceResponse: MutableLiveData<Resource<MarkerLatLngResponse>> =
        MutableLiveData()
    val markerPoliceResponse: LiveData<Resource<MarkerLatLngResponse>>
        get() = _markerPoliceResponse
    private val _walkRewardResponse: MutableLiveData<Resource<WalkRecordResponse>> = MutableLiveData()
    val walkRewardResponse : LiveData<Resource<WalkRecordResponse>>
        get() = _walkRewardResponse

    fun init(mMap: NaverMap) {
        this.mMap = mMap
    }

    private val _mapResponse: MutableLiveData<Resource<MapResponse>> = MutableLiveData()
    val mapResponse: LiveData<Resource<MapResponse>>
        get() = _mapResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getMarkerToilet(type: Int, latitude: String, longitude: String) {
        viewModelScope.launch {
            _markerToiletResponse.value = mainRepository.getMarker(type, latitude, longitude)
        }
    }

    fun getMarkerStore(type: Int, latitude: String, longitude: String) {
        viewModelScope.launch {
            _markerStoreResponse.value = mainRepository.getMarker(type, latitude, longitude)
        }
    }

    fun getMarkerCafe(type: Int, latitude: String, longitude: String) {
        viewModelScope.launch {
            _markerCafeResponse.value = mainRepository.getMarker(type, latitude, longitude)
        }
    }

    fun getMarkerPharmacy(type: Int, latitude: String, longitude: String) {
        viewModelScope.launch {
            _markerPharmacyResponse.value = mainRepository.getMarker(type, latitude, longitude)
        }
    }

    fun getMarkerBicycle(type: Int, latitude: String, longitude: String) {
        viewModelScope.launch {
            _markerBicycleResponse.value = mainRepository.getMarker(type, latitude, longitude)
        }
    }

    fun getMarkerPolice(type: Int, latitude: String, longitude: String) {
        viewModelScope.launch {
            _markerPoliceResponse.value = mainRepository.getMarker(type, latitude, longitude)
        }
    }

    fun getThemeCourse(id: Int) {
        viewModelScope.launch {
            _themeCourseResponse.value = mainRepository.getThemeCourse(id)
        }
    }

    fun getThemeCourseRoute(id: Int) {
        viewModelScope.launch {
            _themeCourseRouteResponse.value = mainRepository.getThemeCourseRoute(id)
        }
    }

    fun setReward(userId: Long, walkCount : Int) {
        viewModelScope.launch {
            _walkRewardResponse.value = mainRepository.setReward(userId, walkCount)
        }
    }
}