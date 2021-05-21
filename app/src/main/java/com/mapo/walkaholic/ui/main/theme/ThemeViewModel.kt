package com.mapo.walkaholic.ui.main.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.FilenameThemeCategoryImageResponse
import com.mapo.walkaholic.data.model.response.ThemeResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ThemeViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }
    private val _themeEnumResponse: MutableLiveData<Resource<FilenameThemeCategoryImageResponse>> = MutableLiveData()
    val themeEnumResponse: LiveData<Resource<FilenameThemeCategoryImageResponse>>
        get() = _themeEnumResponse

    private val _themeResponse: MutableLiveData<Resource<ThemeResponse>> = MutableLiveData()
    val themeResponse: LiveData<Resource<ThemeResponse>>
        get() = _themeResponse

    fun getThemeEnum() {
        viewModelScope.launch {
            _themeEnumResponse.value = mainRepository.getFilenameThemeCategoryImage()
        }
    }

    fun getThemeDetail(themeId: String) {
        viewModelScope.launch {
            _themeResponse.value = mainRepository.getThemeDetail(themeId)
        }
    }
}