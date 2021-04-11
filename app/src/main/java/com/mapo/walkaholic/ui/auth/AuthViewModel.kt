package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.LoginResponse
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(
        private val repository: AuthRepository
) : BaseViewModel() {
    override fun init() {

    }
    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    fun login(id:Long) = viewModelScope.launch {
        _loginResponse.value = repository.login(id)
    }

    fun saveAuthToken(id: Long) = viewModelScope.launch {
        repository.saveAuthToken(id)
    }
}