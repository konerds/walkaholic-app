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
    private val missions: ArrayList<MissionResponse.Mission>
    //private var listener: ChallengeDetailMissionListener
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

/*    fun setItemClickListener(listener: ChallengeDetailMissionListener) {
        this.listener = listener
    }*/

    override fun onBindViewHolder(holder: ItemChallengeMissionViewHolder, position: Int) {
        holder.binding.missionItem = missions[position]
        holder.binding.viewModel

        when (position) {
            0 -> {
                holder.binding.missionStartTv.visibility = View.VISIBLE
                when (missions[position].completeYN) {
                    "0" -> {
                        holder.binding.missionBtn.setEnabled(false)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_uppercorner_c9c3b9)
                    }

                    "1" -> {
                        holder.binding.missionBtn.setEnabled(true)
                        holder.binding.challengeMissionBar.setBackgroundResource(
                            R.drawable.box_challenge_uppercorner_f9a25b)
                        holder.binding.missionNameTv.setTextColor(Color.parseColor("#443F35"))
                    }
                }
            }
            3 -> {
                 /*Drawable 설정 둘 다 가능
                 holder.binding.challengeMissionBar.setBackgroundResource(
                                    R.drawable.selector_challenge_progress)
                 val myImage: Drawable? = ResourcesCompat.getDrawable(
                    holder.binding.root.resources,
                    R.drawable.selector_challenge_progress_bottom, null)
                  holder.binding.challengeMissionBar.background = myImage
                 */

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
            }
        }

        /*holder.binding.missionBtn.setOnClickListener {
            listener?.onItemClick(it, position)
            Log.e("number", "1")
        }*/
    }

    override fun getItemCount() = missions.size
}