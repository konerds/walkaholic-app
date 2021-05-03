package com.mapo.walkaholic.ui.main.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.User

class DashboardThemeRecyclerAdapter(
        var data: LiveData<ArrayList<User>>
) : RecyclerView.Adapter<DashboardThemeRecyclerAdapter.DashboardThemeViewHolder>() {
    inner class DashboardThemeViewHolder constructor(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.themeList, parent, false)
    ) {
        // @TODO Hold Item View
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardThemeViewHolder {
        return DashboardThemeViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DashboardThemeViewHolder, position: Int) {
        data.value!!.get(position).let { item ->
            with(holder) {
                // @TODO Bind Item Data
            }
        }
    }

    override fun getItemCount(): Int {
        return data.value!!.size
    }
}