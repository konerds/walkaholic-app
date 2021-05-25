package com.mapo.walkaholic.data.model.response

data class WalkRecordExistInMonthResponse(
    val code: String,
    val message: String,
    val data: ArrayList<WalkRecordExistInMonth>
) {
    data class WalkRecordExistInMonth(
        val year : String,
        val month : String,
        val date : String
    )
}