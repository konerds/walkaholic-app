package com.mapo.walkaholic.ui.main.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ThemeViewModel(
        private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {
    override fun init() {

    }
    private val _anyResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()
    val anyResponse: LiveData<Resource<Unit>>
        get() = _anyResponse

    fun anyService(
            par1: String,
            par2: String
    ) = viewModelScope.launch {
        // @TODO For Theme Service
    }
}