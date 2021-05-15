package com.mapo.walkaholic.ui.main.dashboard.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.WalkRecord
import com.mapo.walkaholic.databinding.ItemDashboardCalendarBinding

class DashboardCalendarAdapter(
    private val walkRecords: ArrayList<WalkRecord>
) : RecyclerView.Adapter<DashboardCalendarAdapter.DashboardCalendarViewHolder>() {
    inner class DashboardCalendarViewHolder(
        val binding : ItemDashboardCalendarBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DashboardCalendarViewHolder(DataBindingUtil.inflate<ItemDashboardCalendarBinding>(
        LayoutInflater.from(parent.context),
        R.layout.item_dashboard_calendar,
        parent,
        false
    ))

    override fun onBindViewHolder(holder: DashboardCalendarViewHolder, position: Int) {
        holder.binding.recordItem = walkRecords[position]
    }

    override fun getItemCount() = walkRecords.size
}