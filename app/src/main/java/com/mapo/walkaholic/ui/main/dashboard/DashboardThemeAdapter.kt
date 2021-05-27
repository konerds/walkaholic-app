package com.mapo.walkaholic.ui.main.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.response.FilenameThemeCategoryImageResponse
import com.mapo.walkaholic.databinding.ItemDashboardThemeBinding

class DashboardThemeAdapter(
    private val arrayListFilenameThemeCategoryImage: ArrayList<FilenameThemeCategoryImageResponse.FilenameThemeCategoryImage>,
    private val listener: DashboardThemeClickListener
) : RecyclerView.Adapter<DashboardThemeAdapter.DashboardThemeViewHolder>() {
    inner class DashboardThemeViewHolder(
        val binding: ItemDashboardThemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setThemeEnumItem(filenameThemeCategoryImage: FilenameThemeCategoryImageResponse.FilenameThemeCategoryImage) {
            binding.filenameThemeCategoryImage = filenameThemeCategoryImage
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
        holder.setThemeEnumItem(arrayListFilenameThemeCategoryImage[position])

        holder.binding.dashboardThemeCV.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount() = arrayListFilenameThemeCategoryImage.size
}