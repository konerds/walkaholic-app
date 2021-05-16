package com.mapo.walkaholic.ui.main.dashboard.character_shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotShopBinding

class DashboardCharacterShopDetailAdapter(
    private val arrayListItemInfo: ArrayList<ItemInfo>
) : RecyclerView.Adapter<DashboardCharacterShopDetailAdapter.DashboardCharacterShopDetailViewHolder>() {
    inner class DashboardCharacterShopDetailViewHolder(
        val binding: ItemSlotShopBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItemInfo(itemInfo: ItemInfo) {
            binding.itemInfo = itemInfo
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
        holder.setItemInfo(arrayListItemInfo[position])
    }

    override fun getItemCount() = arrayListItemInfo.size
}