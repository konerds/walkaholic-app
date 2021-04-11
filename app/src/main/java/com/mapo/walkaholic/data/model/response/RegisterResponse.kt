package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.User

data class RegisterResponse(
    val error : String,
    val message : String,
    val id : Long
)