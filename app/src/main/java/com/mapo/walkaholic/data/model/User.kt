package com.mapo.walkaholic.data.model

data class User(
        val id: Long,
        val user_nick_name: String,
        val user_gender: String,
        val user_height: String,
        val user_birth: String,
        val user_weight: String,
        val user_current_exp: Long,
        val user_point: Long,
        val user_total_accumulate_point : Long,
        val user_walk_count : Long,
        val user_walk_distance : Long,
        val user_calorie: Long,
        val user_month_point: Long,
        val user_connected_at: String,
        val user_registered_at: String,
        val character_id: Long,
        val level_id: Long
)