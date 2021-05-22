package com.mapo.walkaholic.data.model.response

data class GuideInformationResponse(
    val code: String,
    val message: String,
    val data: ArrayList<GuideInformation>
) {
    data class GuideInformation(
        val tutorialFilename: String
    )
}