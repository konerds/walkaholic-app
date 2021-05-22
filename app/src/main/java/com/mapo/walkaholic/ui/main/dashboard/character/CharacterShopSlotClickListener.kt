package com.mapo.walkaholic.ui.main.dashboard.character

import com.mapo.walkaholic.data.model.ItemInfo

interface CharacterShopSlotClickListener {
    fun onItemClick(selectedItemInfoMap: MutableMap<Int, Triple<Boolean, ItemInfo, Boolean>>)
}