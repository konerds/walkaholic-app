package com.mapo.walkaholic.ui.main.challenge.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.RankingAccumulateResponse
import com.mapo.walkaholic.data.model.response.RankingMonthResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ChallengeDetailRankingViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }

    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _accumulateRankingResponse: MutableLiveData<Resource<RankingAccumulateResponse>> = MutableLiveData()
    val accumulateRankingResponse: LiveData<Resource<RankingAccumulateResponse>>
        get() = _accumulateRankingResponse
    private val _monthRankingResponse: MutableLiveData<Resource<RankingMonthResponse>> = MutableLiveData()
    val monthRankingResponse: LiveData<Resource<RankingMonthResponse>>
        get() = _monthRankingResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getAccumulateRanking() {
        viewModelScope.launch {
            _accumulateRankingResponse.value = mainRepository.getAccumulateRanking()
        }
    }

    fun getMonthRanking() {
        viewModelScope.launch {
            _monthRankingResponse.value = mainRepository.getMonthRanking()
        }
    }


}