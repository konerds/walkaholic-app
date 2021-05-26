package com.mapo.walkaholic.ui.main.challenge.ranking

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.response.RankingResponse
import com.mapo.walkaholic.databinding.ItemChallengeRankingBinding

class ChallengeDetailRankingAdapter(
    private val rankings: ArrayList<RankingResponse.Ranking>
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

        when (rankings[position].rank) {
            "1" -> {
                val crownImg: Drawable? = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.ic_challenge_crown_gold,
                    null
                )
                holder.binding.rankingCrown.setImageDrawable(crownImg)

                /*holder.binding.challengeRankingUserTv.setTextColor(Color.parseColor("#F37520"))
                holder.binding.challengeRankingUserTv2.setTextColor(Color.parseColor("#F37520"))
                holder.binding.challengeRankingPointTv.setTextColor(Color.parseColor("#689F38"))*/
            }
            "2" -> {
                val crownImg: Drawable? = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.ic_challenge_crown_skyblue,
                    null
                )
                holder.binding.rankingCrown.setImageDrawable(crownImg)
            }
            "3" -> {
                val crownImg: Drawable? = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.ic_challenge_crown_orange,
                    null
                )
                holder.binding.rankingCrown.setImageDrawable(crownImg)
            }
            else -> {
                /*holder.binding.challengeRankingUserTv
                holder.binding.challengeRankingUserTv2
                holder.binding.challengeRankingPointTv*/
            }
        }
    }

    override fun getItemCount() = rankings.size
}