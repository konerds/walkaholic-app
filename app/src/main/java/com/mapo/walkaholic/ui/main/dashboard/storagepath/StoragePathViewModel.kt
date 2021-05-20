package com.mapo.walkaholic.ui.main.dashboard.storagepath

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.StoragePathResponse
import com.mapo.walkaholic.data.model.response.ThemeEnumResponse
import com.mapo.walkaholic.data.model.response.UserResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class StoragePathViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _storagePathResponse: MutableLiveData<Resource<StoragePathResponse>> = MutableLiveData()
    val storagePathResponse: LiveData<Resource<StoragePathResponse>>
        get() = _storagePathResponse

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

    fun getStoragePath(userId: Long) {
        viewModelScope.launch {
            _storagePathResponse.value = mainRepository.getStoragePath(userId)
        }
    }
}