package com.mapo.walkaholic.data.model.response

data class TermPrivacyResponse(
    val code: String,
    val message: String,
    val data: ArrayList<TermPrivacy>
) {
    data class TermPrivacy(
        val privacyTermContent: String
    )
}