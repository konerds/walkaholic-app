package com.mapo.walkaholic.ui.main.dashboard.character.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotInventoryBinding
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterItemSlotClickListener

class DashboardCharacterInfoDetailAdapter(
    private var arrayListItemInfo: ArrayList<ItemInfo>,
    private val listener: CharacterItemSlotClickListener
) : RecyclerView.Adapter<DashboardCharacterInfoDetailAdapter.DashboardCharacterInfoDetailViewHolder>() {

    private val selectedSlotInventoryMap = mutableMapOf<Int, Triple<Boolean, ItemInfo, Boolean>>()

    init {
        clearSelectedItem()
    }

    inner class DashboardCharacterInfoDetailViewHolder(
        val binding: ItemSlotInventoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItemInfo(itemInfo: ItemInfo?) {
            if(itemInfo != null) {
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
        if(arrayListItemInfo[position].itemId.isNullOrEmpty()) {
            holder.setItemInfo(null)
        } else {
            holder.setItemInfo(arrayListItemInfo[position])
            holder.binding.itemInventoryLayout.setOnClickListener {
                toggleItemSelected(position)
                listener.onRecyclerViewItemClick(holder.binding.itemInventoryLayout, position, selectedSlotInventoryMap)
            }
        }
    }

    override fun getItemCount() = arrayListItemInfo.size

    /*fun setData(arrayListNewInfoItemInfo: ArrayList<ItemInfo>) {
        arrayListItemInfo = arrayListNewInfoItemInfo
    }*/

    private fun toggleItemSelected(position: Int) {
        if(selectedSlotInventoryMap[position] != null) {
            if (selectedSlotInventoryMap[position]!!.first) {
                clearSelectedItem()
                selectedSlotInventoryMap[position] = Triple(false, selectedSlotInventoryMap[position]!!.second, false)
            } else {
                clearSelectedItem()
                selectedSlotInventoryMap[position] = Triple(true, selectedSlotInventoryMap[position]!!.second, true)
            }
            notifyDataSetChanged()
        } else { }
    }

    private fun isItemSelected(position: Int): Boolean {
        return if (selectedSlotInventoryMap[position] != null) {
            selectedSlotInventoryMap[position]!!.first
        } else {
            false
        }
    }

    private fun clearSelectedItem() {
        for(i in 0 until arrayListItemInfo.size) {
            selectedSlotInventoryMap[i] = Triple(false, arrayListItemInfo[i], false)
        }
    }
}