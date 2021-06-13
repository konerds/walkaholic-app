package com.mapo.walkaholic.data.model.response

data class RegisterResponse(
    val code: String,
    val message: String,
    val data: ArrayList<DataAuth>
) {
    data class DataAuth(
        val userId: Long
    )
}