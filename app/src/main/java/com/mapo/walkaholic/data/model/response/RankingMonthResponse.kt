package com.mapo.walkaholic.data.model.response

class RankingMonthResponse (
    val code: String,
    val message: String,
    val data: ArrayList<Ranking>
) {
    data class Ranking(
        val nickname: String,
        val rank: String,
        val monthPoint: String
    )
}