package com.mapo.walkaholic.data.model.response

data class AccumulateRankingResponse (
    val code: String,
    val message: String,
    val data: ArrayList<AccumulateRanking>
) {
    data class AccumulateRanking(
        val nickName: String,
        val rank: String,
        val totalAccumulatePoint: String
    )
}