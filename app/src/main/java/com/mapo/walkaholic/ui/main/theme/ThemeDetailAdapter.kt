package com.mapo.walkaholic.ui.main.theme

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.databinding.ItemThemeBinding

class ThemeDetailAdapter(
    private val arrayListTheme: ArrayList<Theme>,
    private val listener: ThemeItemClickListener
) : RecyclerView.Adapter<ThemeDetailAdapter.ItemThemeViewHolder>() {
    inner class ItemThemeViewHolder(
        val binding: ItemThemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setThemeItem(themeItem: Theme) {
            binding.themeItem = themeItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemThemeViewHolder {
        val binding = ItemThemeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemThemeViewHolder, position: Int) {
        holder.setThemeItem(arrayListTheme[position])
        holder.binding.itemThemeFL.setOnClickListener {
            listener.onItemClick(position, arrayListTheme[position])
        }
    }

    override fun getItemCount() = arrayListTheme.size
}