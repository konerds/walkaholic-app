package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Theme

data class ThemeResponse(
        val error: Boolean,
        val theme: ArrayList<Theme>?
)