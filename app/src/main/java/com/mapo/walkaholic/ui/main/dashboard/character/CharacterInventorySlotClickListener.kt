package com.mapo.walkaholic.ui.main.dashboard.character

import com.mapo.walkaholic.data.model.ItemInfo

interface CharacterInventorySlotClickListener {
    fun onItemClick(selectedItemInfoMap: MutableMap<Int, Pair<Boolean, ItemInfo>>)
    fun getDash()
}