package com.mapo.walkaholic.data.model.response

data class FilenameLogoImageResponse(
    val code: String,
    val message: String,
    val data: ArrayList<FilenameLogoImage>
) {
    inner class FilenameLogoImage(
        val logoFilename : String
    )
}