package com.mapo.walkaholic.data.model.response

data class MonthRankingResponse(
    val code: String,
    val message: String,
    val data: ArrayList<MonthRanking>
) {
    data class MonthRanking(
        val nickName: String,
        val rank: String,
        val monthPoint: String
    )
}