package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Mission

data class MissionResponse (
    val code: String,
    val message: String,
    val data: ArrayList<Mission>
) {
    data class Mission (
        val missionId: String,
        val missionName: String,
        val completeYN: String,
        val takeRewardYN: String,
        val missionReward: String
    )
}