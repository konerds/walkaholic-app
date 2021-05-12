package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.WalkRecordExistInMonth

data class WalkRecordExistInMonthResponse(
    val error: Boolean,
    val walkRecord: ArrayList<WalkRecordExistInMonth>
)