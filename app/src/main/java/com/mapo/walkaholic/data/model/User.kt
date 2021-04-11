package com.mapo.walkaholic.data.model

data class User(
    val id: Long,
    val registered_at: Long,
    val name: String,
    val nickname: String,
    val birth: Int,
    val gender: Int,
    val height: Int,
    val weight: Int
)