package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.UserCharacter

data class CharacterResponse(
        val error : Boolean,
        val userCharacter : UserCharacter
)