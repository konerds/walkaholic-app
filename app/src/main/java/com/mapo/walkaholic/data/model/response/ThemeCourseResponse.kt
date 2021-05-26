package com.mapo.walkaholic.data.model.response

data class ThemeCourseResponse(
        val code: String,
        val message: String,
        val data: DataThemeCourse
) {
        data class DataThemeCourse(
                val courseId : Int,
                val courseName : String,
                val courseContents : String,
                val courseRouteInfo : String,
                val courseAddress : String,
                val courseFilename : String,
                val courseTime : String,
                val courseDistance : String,
                val coursePoint : String
        )
}