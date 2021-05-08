package com.mapo.walkaholic.ui.main.theme

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.ThemeEnumResponse
import com.mapo.walkaholic.data.model.response.ThemeResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class ThemeDetailViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }

    private val _themeResponse: MutableLiveData<Resource<ThemeResponse>> = MutableLiveData()
    val themeResponse: LiveData<Resource<ThemeResponse>>
        get() = _themeResponse

    fun convertTimeToHour(time: String): String {
        val stringBuilder = StringBuilder()
        if (time[0].toString() == "0") {
            stringBuilder.append(time[1])
        } else {
            stringBuilder.append(time.substring(0, 2))
        }
        stringBuilder.append(".")
        stringBuilder.append(time.substring(3, 5))
        return stringBuilder.toString()
    }

    fun getThemeDetail(themeId: String) {
        viewModelScope.launch {
            _themeResponse.value = mainRepository.getThemeDetail(themeId)
        }
    }
}