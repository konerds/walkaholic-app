package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.ExpTable

data class ExpTableResponse(
        val error: Boolean,
        val exptable: ExpTable
)