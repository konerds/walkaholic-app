package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.CharacterUri

data class CharacterUriResponse(
    val error: Boolean,
    val characterUri: ArrayList<CharacterUri>
)