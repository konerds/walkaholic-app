package com.mapo.walkaholic.ui.main.challenge.mission

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
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
import com.mapo.walkaholic.data.model.response.MissionResponse
import com.mapo.walkaholic.databinding.ItemChallengeMissionBinding
import com.mapo.walkaholic.ui.favoritepath.FavoritePathClickListener

class ChallengeDetailMissionAdapter(
    private val missions: ArrayList<MissionResponse.Mission>,
    private var listener: ChallengeDetailMissionListener
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

        /*var everyMission: Int = 0

        for (i in 0..2) {
            if(missions[i].completeYN == "1" && missions[i].takeRewardYN == "0") {
                everyMission += 1
            }
        }*/

        when (position) {
            0 -> {
                /*Drawable 설정 둘 다 가능
                 holder.binding.challengeMissionBar.setBackgroundResource(
                                    R.drawable.selector_challenge_progress)
                 val myImage: Drawable? = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.selector_challenge_progress_bottom, null)
                  holder.binding.challengeMissionBar.background = myImage
                 */

                holder.binding.missionStartTv.visibility = View.VISIBLE
                when (missions[position].completeYN) {
                    "1" -> {
                        holder.binding.missionBtn.setEnabled(true)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_uppercorner_f9a25b)
                        holder.binding.missionNameTv.setTextColor(Color.parseColor("#443F35"))
                    }
                }
                when (missions[position].takeRewardYN) {
                    "1" -> {
                        holder.binding.missionBtn.setEnabled(false)
                    }
                }
            }
            3 -> {
                holder.binding.challengeMissionBottomBar.visibility = View.GONE
                holder.binding.missionBtn.text = "모든 P 받기"

                when (missions[position].completeYN) {
                    "0" -> {
                        holder.binding.missionBtn.setEnabled(false)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_lowercorner_c9c3b9)
                    }

                    "1" -> {
                        holder.binding.missionBtn.setEnabled(true)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_lowercorner_f9a25b)
                        holder.binding.missionNameTv.setTextColor(Color.parseColor("#443F35"))
                    }
                }
                when (missions[position].takeRewardYN) {
                    "1" -> {
                        holder.binding.missionBtn.setEnabled(false)
                    }
                }
            }
            else -> {
                when (missions[position].completeYN) {
                    "0" -> {
                        holder.binding.missionBtn.setEnabled(false)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_c9c3b9)
                    }

                    "1" -> {
                        holder.binding.missionBtn.setEnabled(true)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_f9a25b)
                        holder.binding.missionNameTv.setTextColor(Color.parseColor("#443F35"))
                    }
                }
                when (missions[position].takeRewardYN) {
                    "1" -> {
                        holder.binding.missionBtn.setEnabled(false)
                    }
                }
            }
        }

        holder.binding.missionBtn.setOnClickListener {
            listener?.onItemClick(it, position)
        }
    }

    override fun getItemCount() = missions.size
}