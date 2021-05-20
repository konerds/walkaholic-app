package com.mapo.walkaholic.ui.main.dashboard.character

import android.util.SparseBooleanArray
import android.view.View
import com.mapo.walkaholic.data.model.ItemInfo

interface CharacterItemSlotClickListener {
    fun onRecyclerViewItemClick(view: View, position: Int, selectedItemInfoMap: MutableMap<Int, Triple<Boolean, ItemInfo, Boolean>>)
}