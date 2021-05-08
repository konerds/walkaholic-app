package com.mapo.walkaholic.data.model.response

data class AuthResponse(
        val error: Boolean,
        val message: String,
        val jwtToken: String
)