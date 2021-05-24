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
}