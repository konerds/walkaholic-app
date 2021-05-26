package com.mapo.walkaholic.data.model

data class User(
    val id: Long,
    val nickName: String,
    val gender: String,
    val currentExp: Int,
    val walkCount: Int,
    val walkDistance: Long,
    val connectedDate: String,
    val height: String,
    val weight: String,
    val petId: Int,
    val levelId: Int,
    val currentPoint: Int,
    val birth: String,
    val petName: String,
    val weeklyMissionAchievement: Int
)