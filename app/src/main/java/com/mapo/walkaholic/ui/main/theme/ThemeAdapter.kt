package com.mapo.walkaholic.ui.main.theme

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.ThemeEnum
import com.mapo.walkaholic.databinding.ItemDashboardThemeBinding
import com.mapo.walkaholic.databinding.ItemThemeBinding

class ThemeAdapter(
    private val themes: ArrayList<ThemeEnum>
) : RecyclerView.Adapter<ThemeAdapter.ItemThemeViewHolder>() {
    inner class ItemThemeViewHolder(
        val binding : ItemThemeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemThemeViewHolder(DataBindingUtil.inflate<ItemThemeBinding>(
        LayoutInflater.from(parent.context),
        R.layout.item_theme,
        parent,
        false
    ))

    override fun onBindViewHolder(holder: ItemThemeViewHolder, position: Int) {
        holder.binding.themeItem = themes[position]
    }

    override fun getItemCount() = themes.size
}