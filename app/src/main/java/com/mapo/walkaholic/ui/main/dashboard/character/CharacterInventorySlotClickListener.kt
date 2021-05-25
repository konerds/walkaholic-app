package com.mapo.walkaholic.ui.main.dashboard.character

import com.mapo.walkaholic.data.model.ItemInfo

interface CharacterInventorySlotClickListener {
    fun onItemClick(
        selectedItemInfoMap: MutableMap<Int, Pair<Boolean, ItemInfo>>,
        isClear: Boolean,
        selectedReverseInfoMap: MutableMap<Int, Pair<Boolean, ItemInfo>>
    )

    fun discardItem(itemId: Int, itemName: String)
}