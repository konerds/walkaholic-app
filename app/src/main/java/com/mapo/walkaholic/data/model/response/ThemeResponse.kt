package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Theme

data class ThemeResponse(
        val code: String,
        val message: String,
        val data: ArrayList<Theme>
)