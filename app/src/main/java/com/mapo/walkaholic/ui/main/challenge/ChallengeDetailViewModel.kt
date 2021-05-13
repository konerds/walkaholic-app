package com.mapo.walkaholic.ui.main.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val _challengeResponse: MutableLiveData<Resource<ThemeResponse>> = MutableLiveData()
    val challengeResponse: LiveData<Resource<ThemeResponse>>
        get() = _challengeResponse

    fun getThemeDetail(themeId: String) {
        viewModelScope.launch {
            _challengeResponse.value = mainRepository.getThemeDetail(themeId)
        }
    }
}