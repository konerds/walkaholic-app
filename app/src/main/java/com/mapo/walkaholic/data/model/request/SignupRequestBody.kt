package com.mapo.walkaholic.data.model.request

class SignupRequestBody(
    private val birth: String,
    private val gender: String,
    private val height: String,
    private val id: Long,
    private val nickName: String,
    private val weight: String
)