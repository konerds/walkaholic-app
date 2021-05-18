package com.mapo.walkaholic.ui.main.challenge.mission

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
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
        val binding: ItemChallengeMissionBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemChallengeMissionViewHolder(
        DataBindingUtil.inflate<ItemChallengeMissionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_challenge_mission,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ItemChallengeMissionViewHolder, position: Int) {
        holder.binding.missionItem = missions[position]
        holder.binding.viewModel

        when (position) {
            0 -> {
                holder.binding.challengeMissionBar.setBackgroundResource(
                    R.drawable.selector_challenge_progress_top
                )
            }
            3 -> {
                val myImage: Drawable? = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.selector_challenge_progress_bottom,
                    null
                )
                holder.binding.challengeMissionBar.background = myImage
                holder.binding.challengeMissionBottomBar.visibility = View.GONE
            }
            else -> {
                holder.binding.challengeMissionBar.setBackgroundResource(
                    R.drawable.selector_challenge_progress
                )
            }
        }

    }

    override fun getItemCount() = missions.size
}