package com.mapo.walkaholic.data.model.response

data class FilenameWeatherResponse(
    val code: String,
    val message: String,
    val data: ArrayList<FilenameWeather>
) {
    inner class FilenameWeather(
        val weatherFilename: String
    )
}