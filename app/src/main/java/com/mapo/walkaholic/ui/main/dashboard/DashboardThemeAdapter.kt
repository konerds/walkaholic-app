package com.mapo.walkaholic.ui.main.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.ThemeEnum
import com.mapo.walkaholic.databinding.ItemDashboardThemeBinding

class DashboardThemeAdapter(
    private val themes: ArrayList<ThemeEnum>
) : RecyclerView.Adapter<DashboardThemeAdapter.DashboardThemeViewHolder>() {
    inner class DashboardThemeViewHolder(
        val binding : ItemDashboardThemeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DashboardThemeViewHolder(DataBindingUtil.inflate<ItemDashboardThemeBinding>(
        LayoutInflater.from(parent.context),
        R.layout.item_dashboard_theme,
        parent,
        false
    ))

    override fun onBindViewHolder(holder: DashboardThemeViewHolder, position: Int) {
        holder.binding.themeItem = themes[position]
    }

    override fun getItemCount() = themes.size
}