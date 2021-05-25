package com.mapo.walkaholic.ui.favoritepath

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.FavoritePathResponse
import com.mapo.walkaholic.data.model.response.ThemeResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class FavoritePathDetailViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse

    private val _favoritePathResponse: MutableLiveData<Resource<FavoritePathResponse>> = MutableLiveData()
    val favoritePathResponse: LiveData<Resource<FavoritePathResponse>>
        get() = _favoritePathResponse

    private val _themeResponse: MutableLiveData<Resource<ThemeResponse>> = MutableLiveData()
    val themeResponse: LiveData<Resource<ThemeResponse>>
        get() = _themeResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getFavoritePath(userId: Long, position: Int) {
        viewModelScope.launch {
            _favoritePathResponse.value = mainRepository.getFavoritePath(userId, position)
        }
    }

    /*fun getThemeDetail(themeId: String) {
        viewModelScope.launch {
            _themeResponse.value = mainRepository.getThemeDetail(themeId)
        }
    }*/
}