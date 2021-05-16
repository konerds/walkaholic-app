package com.mapo.walkaholic.ui.main.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.ThemeEnum
import com.mapo.walkaholic.databinding.ItemDashboardThemeBinding

class DashboardThemeAdapter(
    private val arrayListThemeEnum: ArrayList<ThemeEnum>
) : RecyclerView.Adapter<DashboardThemeAdapter.DashboardThemeViewHolder>() {
    inner class DashboardThemeViewHolder(
        val binding: ItemDashboardThemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setThemeEnumItem(themeEnum: ThemeEnum) {
            binding.themeEnumItem = themeEnum
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardThemeViewHolder {
        val binding = ItemDashboardThemeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardThemeViewHolder, position: Int) {
        holder.setThemeEnumItem(arrayListThemeEnum[position])
    }

    override fun getItemCount() = arrayListThemeEnum.size
}