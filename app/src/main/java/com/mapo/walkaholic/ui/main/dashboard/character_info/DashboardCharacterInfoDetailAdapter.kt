package com.mapo.walkaholic.ui.main.dashboard.character_info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotShopBinding

class DashboardCharacterInfoDetailAdapter(
    private val arrayListInfoItemInfo: ArrayList<ItemInfo>
) : RecyclerView.Adapter<DashboardCharacterInfoDetailAdapter.DashboardCharacterInfoDetailViewHolder>() {
    inner class DashboardCharacterInfoDetailViewHolder(
        val binding: ItemSlotShopBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItemInfo(itemInfo: ItemInfo) {
            binding.itemInfo = itemInfo
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardCharacterInfoDetailViewHolder {
        val binding = ItemSlotShopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardCharacterInfoDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardCharacterInfoDetailViewHolder, position: Int) {
        holder.setItemInfo(arrayListInfoItemInfo[position])
    }

    override fun getItemCount() = arrayListInfoItemInfo.size
}