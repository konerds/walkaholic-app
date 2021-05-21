package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.UserCharacterFilename

data class UserCharacterPreviewFilenameResponse(
        val code: String,
        val message: String,
        val data: ArrayList<UserCharacterFilename>
)