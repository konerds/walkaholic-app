package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.FavoritePath

data class FavoritePathResponse (
    val error: Boolean,
    val FavoritePath: ArrayList<FavoritePath>
)