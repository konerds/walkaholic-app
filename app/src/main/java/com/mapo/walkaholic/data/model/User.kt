package com.mapo.walkaholic.data.model

data class User(
    val connected_at: String,
    val id: Int,
    val kakao_account: KakaoAccount,
    val properties: Properties,
    val userName: String,
    val userNick: String,
    val userHeight: String,
    val userWeight: String
)