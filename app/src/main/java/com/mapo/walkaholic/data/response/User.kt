package com.mapo.walkaholic.data.response

data class User(
    val access_token : String,
    val created_at : String,
    val userId : String,
    val userName: String,
    val userNick: String,
    val userPhone: String,
    val userBirth: String,
    val userGender: String,
    val userHeight: String,
    val userWeight: String
)