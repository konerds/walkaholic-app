package com.mapo.walkaholic.ui.main.challenge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.databinding.ItemChallengeRankingBinding
import com.mapo.walkaholic.databinding.ItemThemeBinding

class ChallengeDetailRankingAdapter(
    private val rankings: ArrayList<Theme>
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
    }

    override fun getItemCount() = rankings.size
}