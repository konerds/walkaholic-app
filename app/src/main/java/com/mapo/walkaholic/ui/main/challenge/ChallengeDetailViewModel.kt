package com.mapo.walkaholic.ui.main.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.MissionConditionResponse
import com.mapo.walkaholic.data.model.response.MissionProgressResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ChallengeDetailViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }

    private val _missionConditionResponse: MutableLiveData<Resource<MissionConditionResponse>> =
        MutableLiveData()
    val missionConditionResponse: LiveData<Resource<MissionConditionResponse>>
        get() = _missionConditionResponse
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _missionProgressResponse: MutableLiveData<Resource<MissionProgressResponse>> =
        MutableLiveData()
    val missionProgressResponse: LiveData<Resource<MissionProgressResponse>>
        get() = _missionProgressResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getMissionCondition(position: Int) {
        viewModelScope.launch {
            _missionConditionResponse.value = mainRepository.getMissionCondition(position)
        }
    }

    fun getMissionProgress(missionID: String, conditionId: String) {
        viewModelScope.launch {
            _missionProgressResponse.value =
                mainRepository.getMissionProgress(missionID, conditionId)
        }
    }
}