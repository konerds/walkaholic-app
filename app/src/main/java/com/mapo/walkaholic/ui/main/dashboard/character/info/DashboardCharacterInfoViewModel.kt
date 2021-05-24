package com.mapo.walkaholic.ui.main.dashboard.character.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DashboardCharacterInfoViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {}
    private val _userResponse: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Resource<UserResponse>>
        get() = _userResponse
    private val _expInformationResponse: MutableLiveData<Resource<ExpInformationResponse>> = MutableLiveData()
    val expInformationResponse: LiveData<Resource<ExpInformationResponse>>
        get() = _expInformationResponse
    private val _userCharacterPreviewFilenameResponse: MutableLiveData<Resource<UserCharacterPreviewFilenameResponse>> = MutableLiveData()
    val userCharacterPreviewFilenameResponse: LiveData<Resource<UserCharacterPreviewFilenameResponse>>
        get() = _userCharacterPreviewFilenameResponse
    private val _userCharacterEquipStatusResponse: MutableLiveData<Resource<UserCharacterEquipStatusResponse>> = MutableLiveData()
    val userCharacterEquipStatusResponse: LiveData<Resource<UserCharacterEquipStatusResponse>>
        get() = _userCharacterEquipStatusResponse
    private val _statusUserCharacterInventoryItemResponse: MutableLiveData<Resource<UserInventoryItemStatusResponse>> = MutableLiveData()
    val statusUserCharacterInventoryItemResponse: LiveData<Resource<UserInventoryItemStatusResponse>>
        get() = _statusUserCharacterInventoryItemResponse
    private val _equipItemResponse : MutableLiveData<Resource<EquipItemResponse>> = MutableLiveData()
    val equipItemResponse: LiveData<Resource<EquipItemResponse>>
        get() = _equipItemResponse
    private val _deleteItemResponse : MutableLiveData<Resource<DeleteItemResponse>> = MutableLiveData()
    val deleteItemResponse: LiveData<Resource<DeleteItemResponse>>
        get() = _deleteItemResponse

    fun getUser() {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _userResponse.value = mainRepository.getUser()
            progressBarVisibility.set(false)
        }
    }

    fun getExpInformation(userId: Long) {
        viewModelScope.launch {
            _expInformationResponse.value = mainRepository.getExpInformation(userId)
        }
    }

    fun getUserCharacterEquipStatus(userId: Long) {
        viewModelScope.launch {
            _userCharacterEquipStatusResponse.value = mainRepository.getUserCharacterEquipStatus(userId)
        }
    }

    fun getUserCharacterPreviewFilename(userId: Long, faceItemId : String, headItemId : String) {
        viewModelScope.launch {
            _userCharacterPreviewFilenameResponse.value = mainRepository.getUserCharacterPreviewFilename(userId, faceItemId, headItemId)
        }
    }

    fun getStatusUserCharacterInventoryItem(userId : Long) {
        viewModelScope.launch {
            _statusUserCharacterInventoryItemResponse.value = mainRepository.getStatusUserCharacterInventoryItem(userId)
        }
    }

    fun equipItem(userId: Long, faceItemId: Int?, hairItemId: Int?) {
        viewModelScope.launch {
            _equipItemResponse.value = mainRepository.equipItem(userId, faceItemId, hairItemId)
        }
    }

    fun deleteItem(userId: Long, itemId: String) {
        viewModelScope.launch {
            _deleteItemResponse.value = mainRepository.deleteItem(userId, itemId)
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()
}