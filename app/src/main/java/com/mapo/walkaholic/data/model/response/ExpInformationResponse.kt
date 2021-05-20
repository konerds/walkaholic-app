package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.ExpInformation

data class ExpInformationResponse(
        val code: String,
        val message: String,
        val data: ArrayList<ExpInformation>
)