package com.mapo.walkaholic.ui.main.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ChallengeDetailViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }

    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _missionResponse: MutableLiveData<Resource<MissionResponse>> =
        MutableLiveData()
    val missionResponse: LiveData<Resource<MissionResponse>>
        get() = _missionResponse
    private val _missionRewardResponse: MutableLiveData<Resource<MissionRewardResponse>> = MutableLiveData()
    val missionRewardResponse: LiveData<Resource<MissionRewardResponse>>
        get() = _missionRewardResponse
    private val _monthRankingResponse: MutableLiveData<Resource<MonthRankingResponse>> = MutableLiveData()
    val monthRankingResponse: LiveData<Resource<MonthRankingResponse>>
        get() = _monthRankingResponse
    private val _accumulateRankingResponse: MutableLiveData<Resource<AccumulateRankingResponse>> = MutableLiveData()
    val accumulateRankingResponse: LiveData<Resource<AccumulateRankingResponse>>
        get() = _accumulateRankingResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getMission(userId: Long, missionType: Int) {
        viewModelScope.launch {
            _missionResponse.value = mainRepository.getMission(userId, missionType)
        }
    }

    fun getMissionReward(userId: Long, missionId: String) {
        viewModelScope.launch {
            _missionRewardResponse.value = mainRepository.getMissionReward(userId, missionId)
        }
    }

    fun getMonthRanking(userId: Long) {
        viewModelScope.launch {
            _monthRankingResponse.value = mainRepository.getMonthRanking(userId)
        }
    }

    fun getAccumulateRanking(userId: Long) {
        viewModelScope.launch {
            _accumulateRankingResponse.value = mainRepository.getAccumulateRanking(userId)
        }
    }
}