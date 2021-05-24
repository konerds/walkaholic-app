package com.mapo.walkaholic.ui.main.dashboard.character.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DashboardCharacterShopDetailViewModel(
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
    private val _statusShopSaleItemResponse: MutableLiveData<Resource<ShopSaleItemStatusResponse>> = MutableLiveData()
    val statusShopSaleItemResponse: LiveData<Resource<ShopSaleItemStatusResponse>>
        get() = _statusShopSaleItemResponse
    private val _buyItemResponse: MutableLiveData<Resource<BuyItemResponse>> = MutableLiveData()
    val buyItemResponse: LiveData<Resource<BuyItemResponse>>
        get() = _buyItemResponse

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

    fun getStatusShopSaleItem() {
        viewModelScope.launch {
            _statusShopSaleItemResponse.value = mainRepository.getStatusShopSaleItem()
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

    fun buyItem(userId: Long, arrayListItemId: ArrayList<Int?>) {
        viewModelScope.launch {
            _buyItemResponse.value = mainRepository.buyItem(userId, arrayListItemId)
        }
    }

    fun toAnyToString(any: Any) = any.toString().trim()
}