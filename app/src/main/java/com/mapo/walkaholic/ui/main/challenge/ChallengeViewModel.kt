package com.mapo.walkaholic.ui.main.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.MissionConditionResponse
import com.mapo.walkaholic.data.model.response.MissionProgressResponse
import com.mapo.walkaholic.data.model.response.RankingResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ChallengeViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }

    private val _missionConditionResponse: MutableLiveData<Resource<MissionConditionResponse>> = MutableLiveData()
    val missionConditionResponse: LiveData<Resource<MissionConditionResponse>>
        get() = _missionConditionResponse
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _missionProgressResponse: MutableLiveData<Resource<MissionProgressResponse>> = MutableLiveData()
    val missionProgressResponse: LiveData<Resource<MissionProgressResponse>>
        get() = _missionProgressResponse
    private val _rankingResponse: MutableLiveData<Resource<RankingResponse>> = MutableLiveData()
    val rankingResponse: LiveData<Resource<RankingResponse>>
        get() = _rankingResponse

    fun getUser() {
        progressBarVisibility.set(true)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            viewModelScope.launch {
                if (error != null) {
                } else {
                    _userResponse.value = tokenInfo?.id?.let { mainRepository.getUser(it) }
                }
                progressBarVisibility.set(false)
            }
        }
    }

    fun getMissionCondition(missionID: String) {
        viewModelScope.launch {
            _missionConditionResponse.value = mainRepository.getMissionCondition(missionID)
        }
    }

    fun getMissionProgress(missionID: String, conditionId: String) {
        viewModelScope.launch {
            _missionProgressResponse.value = mainRepository.getMissionProgress(missionID, conditionId)
        }
    }

    fun getRanking(rankingId: String) {
        viewModelScope.launch {
            _rankingResponse.value = mainRepository.getRanking(rankingId)
        }
    }
}