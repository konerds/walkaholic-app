package com.mapo.walkaholic.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.mapo.walkaholic.data.model.response.*
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class DashboardThemeItemViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {}
    private val _themeEnumResponse: MutableLiveData<Resource<ThemeEnumResponse>> = MutableLiveData()
    val themeEnumResponse: LiveData<Resource<ThemeEnumResponse>>
        get() = _themeEnumResponse

    fun toAnyToString(any: Any) = any.toString().trim()

    fun getThemeList(type: String) : String {
        return when(type) {
            "00" -> "힐링"
            "01" -> "데이트"
            "02" -> "운동"
            else -> "오류"
        }
    }
}