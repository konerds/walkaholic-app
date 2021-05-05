package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.ThemeEnum

data class ThemeEnumResponse(
        val error: Boolean,
        val themeEnum: ArrayList<ThemeEnum>?
)