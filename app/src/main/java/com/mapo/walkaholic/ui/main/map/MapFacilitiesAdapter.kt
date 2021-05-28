package com.mapo.walkaholic.ui.main.map

import android.annotation.SuppressLint
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.MapFacilities
import com.mapo.walkaholic.databinding.ItemMapFacilitiesBinding

class MapFacilitiesAdapter(
    private val arrayListFacilities: ArrayList<MapFacilities>,
    private val listener: MapFacilitiesClickListener
) : RecyclerView.Adapter<MapFacilitiesAdapter.ItemMapFacilitiesViewHolder>() {
    inner class ItemMapFacilitiesViewHolder(
        val binding: ItemMapFacilitiesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setMapFacilitiesItem(mapFacilities: MapFacilities) {
            binding.mapFacilitiesItem = mapFacilities
            binding.itemMapFacilitiesIv.setBackgroundResource(mapFacilities.facilitiesResourceId)
        }
    }

    private val mSelectedMapFacilitiesItems = SparseBooleanArray(itemCount)

    private fun toggleItemSelected(position: Int) {
        if (mSelectedMapFacilitiesItems.get(position, false)) {
            mSelectedMapFacilitiesItems.delete(position)
        } else {
            mSelectedMapFacilitiesItems.put(position, true)
        }
    }

    private fun isItemSelected(position: Int): Boolean {
        return mSelectedMapFacilitiesItems.get(position, false)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemMapFacilitiesViewHolder {
        val binding = ItemMapFacilitiesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemMapFacilitiesViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemMapFacilitiesViewHolder, position: Int) {
        holder.setMapFacilitiesItem(arrayListFacilities[position])
        holder.binding.itemMapFacilitiesLayout.setOnClickListener {
            toggleItemSelected(position)
            holder.binding.itemMapFacilitiesIv.isSelected = isItemSelected(position)
            notifyItemChanged(position)
            listener.onItemClick(
                position,
                arrayListFacilities[position].facilitiesId,
                isItemSelected(position)
            )
        }
    }

    override fun getItemCount() = arrayListFacilities.size
}