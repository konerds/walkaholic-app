package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.WalkRecordExistInMonth

data class WalkRecordExistInMonthResponse(
    val code: String,
    val message: String,
    val data: ArrayList<WalkRecordExistInMonth>
)