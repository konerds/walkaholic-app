package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.MissionProgress

data class MissionProgressResponse (
    val error: Boolean,
    val missionProgress: ArrayList<MissionProgress>
)