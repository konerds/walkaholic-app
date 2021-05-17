package com.mapo.walkaholic.ui.main.challenge.mission

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.MissionCondition
import com.mapo.walkaholic.data.model.MissionDaily
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.databinding.ItemChallengeMissionBinding

class ChallengeDetailMissionAdapter(
    private val missions: ArrayList<MissionCondition>
) : RecyclerView.Adapter<ChallengeDetailMissionAdapter.ItemChallengeMissionViewHolder>() {
    inner class ItemChallengeMissionViewHolder(
        val binding : ItemChallengeMissionBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemChallengeMissionViewHolder(DataBindingUtil.inflate<ItemChallengeMissionBinding>(
        LayoutInflater.from(parent.context),
        R.layout.item_challenge_mission,
        parent,
        false
    ))

    override fun onBindViewHolder(holder: ItemChallengeMissionViewHolder, position: Int) {
        holder.binding.missionItem = missions[position]
        holder.binding.viewModel
    }

    override fun getItemCount() = missions.size
}