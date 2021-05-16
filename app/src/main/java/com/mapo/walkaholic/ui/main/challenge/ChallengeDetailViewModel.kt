package com.mapo.walkaholic.ui.main.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.MissionConditionResponse
import com.mapo.walkaholic.data.model.response.MissionDailyResponse
import com.mapo.walkaholic.data.model.response.ThemeResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ChallengeDetailViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }

    private val _missionConditionResponse: MutableLiveData<Resource<MissionConditionResponse>> = MutableLiveData()
    val missionConditionResponse: LiveData<Resource<MissionConditionResponse>>
        get() = _missionConditionResponse

    fun getMissionCondition(missionID: String) {
        viewModelScope.launch {
            _missionConditionResponse.value = mainRepository.getMissionCondition(missionID)
        }
    }
}