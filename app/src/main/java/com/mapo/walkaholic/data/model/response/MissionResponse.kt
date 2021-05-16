package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Mission

data class MissionResponse (
    val error: Boolean,
    val mission: ArrayList<Mission>
)