package com.mapo.walkaholic.data.model.response

data class CategoryThemeResponse(
    val code: String,
    val message: String,
    val data: ArrayList<CategoryTheme>
) {
    inner class CategoryTheme(
        val themeName: String
    )
}