package com.mapo.walkaholic.data.model.response

data class FilenameThemeCategoryImageResponse(
        val code: String,
        val message: String,
        val data: ArrayList<FilenameThemeCategoryImage>
) {
        inner class FilenameThemeCategoryImage(
                val themeFilename: String
        )
}