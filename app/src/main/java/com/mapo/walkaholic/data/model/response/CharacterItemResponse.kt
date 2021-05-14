package com.mapo.walkaholic.data.model.response
import com.mapo.walkaholic.data.model.CharacterItem

data class CharacterItemResponse(
        val error: Boolean,
        val characterItem: CharacterItem
)