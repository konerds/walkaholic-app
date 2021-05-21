package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.ItemInfo

data class UserInventoryItemStatusResponse(
    val code: String,
    val message: String,
    val data: ArrayList<ItemInfo>
)