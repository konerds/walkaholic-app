package com.mapo.walkaholic.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.data.response.AuthResponse
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val authResponse: LiveData<Resource<AuthResponse>>
        get() = _authResponse

    fun login(
        userId: String,
        userPassword: String
    ) = viewModelScope.launch {
        _authResponse.value = repository.login(userId, userPassword)
    }

    fun saveAuthToken(token: String) = viewModelScope.launch {
        repository.saveAuthToken(token)
    }

    fun register(
        userId: String,
        userPassword: String,
        userName: String,
        userNick: String,
        userPhone: String,
        userBirth: String,
        userGender: String,
        userHeight: String,
        userWeight: String
    ) = viewModelScope.launch {
        _authResponse.value = repository.register(
            userId,
            userPassword,
            userName,
            userNick,
            userPhone,
            userBirth,
            userGender,
            userHeight,
            userWeight
        )
    }
}