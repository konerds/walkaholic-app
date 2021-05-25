package com.mapo.walkaholic.data.model.response

data class WalkRecordResponse(
        val code: String,
        val message: String,
        val totalRecord: TotalRecord,
        val data: ArrayList<DataWalkRecord>
) {
        data class TotalRecord(
                val walkDate: String,
                val walkDay: String,
                val totalWalkTime: String,
                val totalDistance: String,
                val totalWalkCount: String,
                val totalWalkCalorie: String
        )
        data class DataWalkRecord(
                val walkDate: String,
                val walkStartTime: String,
                val walkEndTime: String,
                val walkTime: String,
                val walkDistance: String,
                val walkCount: String,
                val walkCalorie: String,
                val walkFileName: String
        )
}