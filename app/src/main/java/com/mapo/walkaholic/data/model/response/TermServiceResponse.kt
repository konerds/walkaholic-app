package com.mapo.walkaholic.data.model.response

data class TermServiceResponse(
    val code: String,
    val message: String,
    val data: ArrayList<TermService>
) {
    inner class TermService(
        val serviceTermContent: String
    )
}