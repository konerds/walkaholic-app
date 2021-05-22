package com.mapo.walkaholic.data.model.response

data class FilenameLogoImageResponse(
    val code: String,
    val message: String,
    val data: ArrayList<FilenameLogoImage>
) {
    data class FilenameLogoImage(
        val logoFilename : String
    )
}