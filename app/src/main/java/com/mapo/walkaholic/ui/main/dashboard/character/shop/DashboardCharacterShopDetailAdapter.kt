package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.util.SparseBooleanArray
import android.util.SparseIntArray
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

    private val selectedItems: SparseBooleanArray = SparseBooleanArray(0)
    private var selectedTotalPrice: Int = 0

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
                selectedTotalPrice += arrayListItemInfo[position].itemPrice!!.toInt()
                listener.onRecyclerViewItemClick(holder.binding.itemShopLayout, position, arrayListItemInfo, selectedItems, selectedTotalPrice)
            }
        }
    }

    override fun getItemCount() = arrayListItemInfo.size

    fun setData(arrayListNewInfoItemInfo: ArrayList<ItemInfo>) {
        arrayListItemInfo = arrayListNewInfoItemInfo
    }

    private fun toggleItemSelected(position: Int) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
            notifyItemChanged(position)
        } else {
            selectedItems.put(position, true)
            notifyItemChanged(position)
        }
    }

    private fun isItemSelected(position: Int): Boolean {
        return selectedItems.get(position, false)
    }

    fun clearSelectedItem() {
        var position: Int
        for (i in 0 until selectedItems.size()) {
            position = selectedItems.keyAt(i)
            selectedItems.put(position, false)
            notifyItemChanged(position)
        }
        selectedItems.clear()
    }
}