package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.MissionWeekly

data class MissionWeeklyResponse (
    val error: Boolean,
    val missionWeekly: ArrayList<MissionWeekly>
)