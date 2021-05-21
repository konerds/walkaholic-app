package com.mapo.walkaholic.ui.main.dashboard.storagepath

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.StoragePath
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.databinding.ItemStoragePathBinding
import com.mapo.walkaholic.databinding.ItemThemeBinding
import com.mapo.walkaholic.ui.main.theme.ThemeDetailAdapter

class StoragePathDetailAdapter (
    private val arrayListStoragePath: ArrayList<Theme>
) : RecyclerView.Adapter<StoragePathDetailAdapter.ItemStoragePathViewHolder>() {
    inner class ItemStoragePathViewHolder(
        val binding: ItemStoragePathBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setStoragePathItem(storagePathItem: Theme) {
            binding.storagePathItem = storagePathItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemStoragePathViewHolder {
        val binding = ItemStoragePathBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemStoragePathViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemStoragePathViewHolder, position: Int) {
        holder.setStoragePathItem(arrayListStoragePath[position])
    }

    override fun getItemCount() = arrayListStoragePath.size
}