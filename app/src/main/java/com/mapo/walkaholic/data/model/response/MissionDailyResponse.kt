package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.MissionDaily

data class MissionDailyResponse (
    val error: Boolean,
    val missionDaily: ArrayList<MissionDaily>
)