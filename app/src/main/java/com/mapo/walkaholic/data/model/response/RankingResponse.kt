package com.mapo.walkaholic.data.model.response

data class RankingResponse (
    val code: String,
    val message: String,
    val data: ArrayList<Ranking>
) {
    data class Ranking(
        val nickName: String,
        val rank: String,
        val point: String
    )
}