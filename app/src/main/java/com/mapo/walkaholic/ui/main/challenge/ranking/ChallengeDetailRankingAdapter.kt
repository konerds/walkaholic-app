package com.mapo.walkaholic.ui.main.challenge.ranking

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.Ranking
import com.mapo.walkaholic.databinding.ItemChallengeRankingBinding

class ChallengeDetailRankingAdapter(
    private val rankings: ArrayList<Ranking>
) : RecyclerView.Adapter<ChallengeDetailRankingAdapter.ItemChallengeRankingViewHolder>() {
    inner class ItemChallengeRankingViewHolder(
        val binding : ItemChallengeRankingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemChallengeRankingViewHolder(DataBindingUtil.inflate<ItemChallengeRankingBinding>(
        LayoutInflater.from(parent.context),
        R.layout.item_challenge_ranking,
        parent,
        false
    ))

    override fun onBindViewHolder(holder: ItemChallengeRankingViewHolder, position: Int) {
        holder.binding.rankingItem = rankings[position]

        when (position) {
            0 -> {
                holder.binding.rankingCrown.setBackgroundResource(
                    R.drawable.ic_challenge_crown_gold
                )
            }
            1 -> {
                holder.binding.rankingCrown.setBackgroundResource(
                    R.drawable.ic_challenge_crown_skyblue
                )
            }
            2 -> {
                holder.binding.rankingCrown.setBackgroundResource(
                    R.drawable.ic_challenge_crown_orange
                )
            }
            else -> {
                holder.binding.rankingCrown.setBackgroundResource(
                    R.drawable.ic_challenge_crown_gray
                )
            }
        }
    }

    override fun getItemCount() = rankings.size
}