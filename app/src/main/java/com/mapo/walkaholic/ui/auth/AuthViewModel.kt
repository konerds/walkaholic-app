package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.model.response.LoginResponse
import com.mapo.walkaholic.data.model.response.RegisterResponse
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

    private val _registerResponse: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<RegisterResponse>>
        get() = _registerResponse


    fun login(id: Long) = viewModelScope.launch {
        _loginResponse.value = repository.login(id)
    }

    fun register(
        id: Long,
        name: String,
        nickname: String,
        birth: Int,
        gender: Int,
        height: Int,
        weight: Int
    ) = viewModelScope.launch {
        _registerResponse.value =
            repository.register(id, name, nickname, birth, gender, height, weight)
    }

    fun saveAuthToken(id: Long) = viewModelScope.launch {
        repository.saveAuthToken(id)
    }
}