package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.Ranking

data class RankingResponse (
    val error: Boolean,
    val ranking: ArrayList<Ranking>
)