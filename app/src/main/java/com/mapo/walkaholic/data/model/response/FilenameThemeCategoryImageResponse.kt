package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.ThemeEnum

data class FilenameThemeCategoryImageResponse(
        val code: String,
        val message: String,
        val data: ArrayList<FilenameThemeCategoryImage>
) {
        inner class FilenameThemeCategoryImage(
                val themeFilename: String
        )
}