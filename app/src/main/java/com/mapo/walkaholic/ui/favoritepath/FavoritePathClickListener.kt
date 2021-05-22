package com.mapo.walkaholic.ui.favoritepath

import com.mapo.walkaholic.data.model.Theme

interface FavoritePathClickListener {
    fun onItemClick(checkedFavoritePathMap: MutableMap<Int, Pair<Boolean, Theme>>)
}