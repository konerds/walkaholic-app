package com.mapo.walkaholic.data.model.response

import com.google.gson.annotations.SerializedName
import com.mapo.walkaholic.data.model.WalkRecord

data class WalkRecordResponse(
    val error: Boolean,
    val walkRecord: ArrayList<WalkRecord>
)