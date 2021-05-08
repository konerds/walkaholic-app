package com.mapo.walkaholic.ui.auth

import com.mapo.walkaholic.data.repository.AuthRepository
import com.mapo.walkaholic.ui.base.BaseViewModel

class AuthViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    override fun init() {}
}