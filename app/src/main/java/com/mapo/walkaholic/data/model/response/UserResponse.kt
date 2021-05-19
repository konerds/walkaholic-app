package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.User

data class UserResponse(
        val code : String,
        val message : String,
        val data: User?
)