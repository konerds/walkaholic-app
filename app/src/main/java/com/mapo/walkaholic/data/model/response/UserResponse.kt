package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.User
import com.mapo.walkaholic.data.model.UserCharacter

data class UserResponse(
    val error : Boolean,
    val user : User,
    val userCharacter : UserCharacter
)