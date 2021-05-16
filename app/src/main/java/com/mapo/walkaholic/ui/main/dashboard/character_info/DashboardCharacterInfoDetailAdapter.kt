package com.mapo.walkaholic.ui.main.dashboard.character_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotInventoryBinding
import com.mapo.walkaholic.databinding.ItemSlotShopBinding

class DashboardCharacterInfoDetailAdapter(
    private val arrayListInfoItemInfo: ArrayList<ItemInfo>
) : RecyclerView.Adapter<DashboardCharacterInfoDetailAdapter.DashboardCharacterInfoDetailViewHolder>() {
    inner class DashboardCharacterInfoDetailViewHolder(
        val binding: ItemSlotInventoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItemInfo(itemInfo: ItemInfo?) {
            if(itemInfo != null) {
                binding.itemInfo = itemInfo
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
        if(arrayListInfoItemInfo[position].itemId.isNullOrEmpty()) {
            holder.setItemInfo(null)
        } else {
            holder.setItemInfo(arrayListInfoItemInfo[position])
        }
    }

    override fun getItemCount() = arrayListInfoItemInfo.size
}