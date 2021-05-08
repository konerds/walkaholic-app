package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.User

data class UserResponse(
        val error: Boolean,
        val user: User,
        val jwtToken: String
)