package com.mapo.walkaholic.data.model.response

data class ExpInformationResponse(
    val code: String,
    val message: String,
    val data: ArrayList<ExpInformation>
) {
    data class ExpInformation(
        val currentLevelNeedExp: Int,
        val nextLevelNeedExp: Int,
        val nextLevel: Int,
        val nextLevelExp: Int
    )
}