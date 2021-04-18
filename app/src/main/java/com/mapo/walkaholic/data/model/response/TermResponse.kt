package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Terms

data class TermResponse(
        val error: Boolean,
        val terms: Terms
)