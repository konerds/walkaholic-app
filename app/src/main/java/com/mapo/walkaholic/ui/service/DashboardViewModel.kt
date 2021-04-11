package com.mapo.walkaholic.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.LoginResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.BaseRepository
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class DashboardViewModel(
        private val repository: DashboardRepository
) : BaseViewModel() {
    override fun init() {

    }

    private val _user: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val user: LiveData<Resource<LoginResponse>>
        get() = _user

    fun getUser(id:Long) = viewModelScope.launch {
        _user.value = Resource.Loading
        _user.value = repository.getUser(id)
    }
}