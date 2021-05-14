package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.GuideInformation

data class GuideInformationResponse(
    val error: Boolean,
    val guideInformation: ArrayList<GuideInformation>
)