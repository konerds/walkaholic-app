package com.mapo.walkaholic.data.model.response

data class RankingAccumulateResponse (
    val code: String,
    val message: String,
    val data: ArrayList<Ranking>
) {
    data class Ranking(
        val nickname: String,
        val rank: String,
        val totalAccumulatePoint: String
    )
}