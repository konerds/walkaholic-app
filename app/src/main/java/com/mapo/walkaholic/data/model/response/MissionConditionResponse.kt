package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.MissionCondition

class MissionConditionResponse(
    val error: Boolean,
    val missionCondition: ArrayList<MissionCondition>
)