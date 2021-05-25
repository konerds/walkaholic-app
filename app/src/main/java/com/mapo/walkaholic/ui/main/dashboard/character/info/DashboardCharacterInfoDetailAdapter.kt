package com.mapo.walkaholic.ui.main.dashboard.character.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotInventoryBinding
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterInventorySlotClickListener

class DashboardCharacterInfoDetailAdapter(
    private var arrayListItemInfo: ArrayList<ItemInfo>,
    private var arrayListReverseItemInfo: ArrayList<ItemInfo>,
    private val listener: CharacterInventorySlotClickListener
) : RecyclerView.Adapter<DashboardCharacterInfoDetailAdapter.DashboardCharacterInfoDetailViewHolder>() {

    private val selectedSlotInventoryMap = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()
    private val selectedReverseSlotInventoryMap = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()

    init {
        clearSelectedItem()
        clearSelectedReverseItem()
    }

    inner class DashboardCharacterInfoDetailViewHolder(
        val binding: ItemSlotInventoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItemInfo(itemInfo: ItemInfo?) {
            if (itemInfo != null) {
                binding.itemInfo = itemInfo
                binding.itemInventoryIvBase.isSelected = isItemSelected(position)
                binding.itemInventoryIvLowerCorner.isSelected = isItemSelected(position)
                binding.itemInventoryTvEmpty.visibility = View.GONE
                binding.itemInventoryIvDiscard.visibility = View.VISIBLE
                binding.itemInventoryIvLowerCorner.visibility = View.VISIBLE
                binding.itemInventoryIv.visibility = View.VISIBLE
                binding.itemInventoryTvName.visibility = View.VISIBLE
            } else {
                binding.itemInventoryTvEmpty.visibility = View.VISIBLE
                binding.itemInventoryIvDiscard.visibility = View.GONE
                binding.itemInventoryIvLowerCorner.visibility = View.GONE
                binding.itemInventoryIv.visibility = View.GONE
                binding.itemInventoryTvName.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardCharacterInfoDetailViewHolder {
        val binding = ItemSlotInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardCharacterInfoDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardCharacterInfoDetailViewHolder, position: Int) {
        if (arrayListItemInfo[position].itemName.isNullOrEmpty()) {
            holder.setItemInfo(null)
        } else {
            holder.setItemInfo(arrayListItemInfo[position])
            holder.binding.itemInventoryLayout.setOnClickListener {
                toggleItemSelected(position)
                listener.onItemClick(
                    selectedSlotInventoryMap,
                    false,
                    selectedReverseSlotInventoryMap
                )
            }
            holder.binding.itemInventoryIvDiscard.setOnClickListener {
                arrayListItemInfo[position].itemId?.let { _itemId ->
                    arrayListItemInfo[position].itemName?.let { _itemName ->
                        listener.discardItem(
                            _itemId,
                            _itemName
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount() = arrayListItemInfo.size

    private fun toggleItemSelected(position: Int) {
        if (selectedSlotInventoryMap[position] != null) {
            if (selectedSlotInventoryMap[position]!!.first) {
                clearSelectedItem()
                selectedSlotInventoryMap[position] =
                    Pair(false, selectedSlotInventoryMap[position]!!.second)
            } else {
                clearSelectedItem()
                selectedSlotInventoryMap[position] =
                    Pair(true, selectedSlotInventoryMap[position]!!.second)
            }
            notifyDataSetChanged()
        } else {
        }
    }

    private fun isItemSelected(position: Int): Boolean {
        return if (selectedSlotInventoryMap[position] != null) {
            selectedSlotInventoryMap[position]!!.first
        } else {
            false
        }
    }

    fun clearSelectedItem() {
        for (i in 0 until arrayListItemInfo.size) {
            selectedSlotInventoryMap[i] = Pair(false, arrayListItemInfo[i])
        }
    }

    private fun clearSelectedReverseItem() {
        for (i in 0 until arrayListReverseItemInfo.size) {
            selectedReverseSlotInventoryMap[i] = Pair(false, arrayListReverseItemInfo[i])
        }
    }

    fun getData() = selectedSlotInventoryMap

    fun getReverseData(): MutableMap<Int, Pair<Boolean, ItemInfo>> {
        return selectedReverseSlotInventoryMap
    }

    fun setData(selectedStatusEquip: MutableMap<Int, Pair<Boolean, ItemInfo>>) {
        for (i in 0 until arrayListItemInfo.size) {
            if (selectedStatusEquip[i]?.second?.itemType != null && selectedStatusEquip[i]?.second?.itemId != null) {
                selectedSlotInventoryMap[i] = selectedStatusEquip[i] as Pair<Boolean, ItemInfo>
            }
        }
        notifyDataSetChanged()
    }

    fun setReverseData(selectedReverseStatusEquip: MutableMap<Int, Pair<Boolean, ItemInfo>>) {
        for (i in 0 until arrayListReverseItemInfo.size) {
            if (selectedReverseStatusEquip[i]?.second?.itemType != null && selectedReverseStatusEquip[i]?.second?.itemId != null) {
                selectedReverseSlotInventoryMap[i] =
                    selectedReverseStatusEquip[i] as Pair<Boolean, ItemInfo>
            }
        }
        notifyDataSetChanged()
    }
}