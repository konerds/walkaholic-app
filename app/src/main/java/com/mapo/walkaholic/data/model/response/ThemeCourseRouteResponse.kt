package com.mapo.walkaholic.data.model.response

data class ThemeCourseRouteResponse(
    val code: String,
    val message: String,
    val data: ArrayList<DataThemeCourseRoute>
) {
    data class DataThemeCourseRoute(
        val name: String,
        val x: String,
        val y: String
    )
}