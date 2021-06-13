package com.mapo.walkaholic.data.model.response

data class LoginResponse(
    val code: String,
    val message: String,
    val data: ArrayList<DataAuth>
) {
    data class DataAuth(
        val token: String
    )
}