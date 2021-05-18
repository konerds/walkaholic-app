package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotShopBinding
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterItemSlotClickListener

class DashboardCharacterShopDetailAdapter(
    private var arrayListItemInfo: ArrayList<ItemInfo>,
    private val listener: CharacterItemSlotClickListener
) : RecyclerView.Adapter<DashboardCharacterShopDetailAdapter.DashboardCharacterShopDetailViewHolder>() {

    private val selectedSlotShopMap = mutableMapOf<Int, Pair<Boolean, ItemInfo>>()

    init {
        clearSelectedItem()
    }

    inner class DashboardCharacterShopDetailViewHolder(
        val binding: ItemSlotShopBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItemInfo(itemInfo: ItemInfo) {
            if(itemInfo != null) {
                binding.itemInfo = itemInfo
                binding.itemShopIvBase.isSelected = isItemSelected(position)
                binding.itemShopIvLowerCorner.isSelected = isItemSelected(position)
            } else { }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardCharacterShopDetailViewHolder {
        val binding = ItemSlotShopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardCharacterShopDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardCharacterShopDetailViewHolder, position: Int) {
        if(arrayListItemInfo[position].itemId.isNullOrEmpty()) { }
        else {
            holder.setItemInfo(arrayListItemInfo[position])
            holder.binding.itemShopLayout.setOnClickListener {
                toggleItemSelected(position)
                listener.onRecyclerViewItemClick(holder.binding.itemShopLayout, position, selectedSlotShopMap)
            }
        }
    }

    override fun getItemCount() = arrayListItemInfo.size

    fun setData(arrayListNewInfoItemInfo: ArrayList<ItemInfo>) {
        arrayListItemInfo = arrayListNewInfoItemInfo
    }

    private fun toggleItemSelected(position: Int) {
        if(selectedSlotShopMap[position] != null) {
            if (selectedSlotShopMap[position]!!.first) {
                selectedSlotShopMap[position] = Pair(false, selectedSlotShopMap[position]!!.second)
            } else {
                selectedSlotShopMap[position] = Pair(true, selectedSlotShopMap[position]!!.second)
            }
            notifyItemChanged(position)
        } else { }
    }

    private fun isItemSelected(position: Int): Boolean {
        return if (selectedSlotShopMap[position] != null) {
            selectedSlotShopMap[position]!!.first
        } else {
            false
        }
    }

    fun clearSelectedItem() {
        for(i in 0 until arrayListItemInfo.size) {
            selectedSlotShopMap[i] = Pair(false, arrayListItemInfo[i])
        }
    }
}