package com.mapo.walkaholic.ui.main.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.CategoryThemeResponse
import com.mapo.walkaholic.data.model.response.ThemeResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ThemeDetailViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }
    private val _categoryThemeResponse: MutableLiveData<Resource<CategoryThemeResponse>> = MutableLiveData()
    val categoryThemeResponse: LiveData<Resource<CategoryThemeResponse>>
        get() = _categoryThemeResponse
    private val _themeResponse: MutableLiveData<Resource<ThemeResponse>> = MutableLiveData()
    val themeResponse: LiveData<Resource<ThemeResponse>>
        get() = _themeResponse

    fun getCategoryTheme() {
        viewModelScope.launch {
            _categoryThemeResponse.value = mainRepository.getCategoryTheme()
        }
    }

    fun getTheme(position: Int) {
        viewModelScope.launch {
            _themeResponse.value = mainRepository.getTheme(position)
        }
    }
}