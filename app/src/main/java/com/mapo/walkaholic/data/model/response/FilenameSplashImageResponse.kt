package com.mapo.walkaholic.data.model.response

data class FilenameSplashImageResponse(
    val code: String,
    val message: String,
    val data: ArrayList<SplashFilename>
) {
    inner class SplashFilename(
        val splashFilename : String
    )
}