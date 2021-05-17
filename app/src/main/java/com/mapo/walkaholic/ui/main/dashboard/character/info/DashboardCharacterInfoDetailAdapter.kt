package com.mapo.walkaholic.ui.main.dashboard.character.info

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.databinding.ItemSlotInventoryBinding
import com.mapo.walkaholic.ui.base.BaseViewModel
import com.mapo.walkaholic.ui.base.EventObserver

class DashboardCharacterInfoDetailAdapter(
    private var arrayListItemInfo: ArrayList<ItemInfo>,
    val viewModel : DashboardCharacterInfoViewModel
) : RecyclerView.Adapter<DashboardCharacterInfoDetailAdapter.DashboardCharacterInfoDetailViewHolder>() {

    init {
        viewModel.initSelectedInventoryItem(arrayListItemInfo.size, arrayListItemInfo)
    }

    private val selectedItems: SparseBooleanArray = SparseBooleanArray(0)

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
            holder.binding.viewModel = viewModel
            holder.binding.lifecycleOwner?.let { holderLifecycleOwner ->
                viewModel.selectedInventoryItem.observe(holderLifecycleOwner, Observer { _selectedInventoryItem ->
                    holder.binding.itemInventoryLayout.setOnClickListener {
                        if(_selectedInventoryItem.first) {
                            viewModel.unselectSelectedInventoryItem(position, arrayListItemInfo[position])
                        } else {
                            viewModel.selectSelectedInventoryItem(position, arrayListItemInfo[position])
                        }
                        notifyItemChanged(position)
                    }
                })
            }
            holder.binding.viewModel!!.getSelectedInventoryItem(position)
        }
    }

    override fun getItemCount() = arrayListItemInfo.size

    private fun toggleItemSelected(position: Int) {

        viewModel.getSelectedInventoryItem(position)
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