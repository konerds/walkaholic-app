package com.mapo.walkaholic.ui.main.theme

import com.mapo.walkaholic.data.model.Theme

interface ThemeItemClickListener {
    fun onItemClick(position: Int, themeItemInfo: Theme)
}