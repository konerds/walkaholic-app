package com.mapo.walkaholic.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapo.walkaholic.data.model.response.GuideInformationResponse
import com.mapo.walkaholic.databinding.ItemGuideBinding

class GuideAdapter(
    private val arrayListGuideInformation: ArrayList<GuideInformationResponse.GuideInformation>
) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {
    inner class GuideViewHolder(val binding: ItemGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setGuideInformation(guideInformation: GuideInformationResponse.GuideInformation) {
            binding.guideInformation = guideInformation
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): GuideViewHolder {
        val binding = ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: GuideViewHolder,
        position: Int
    ) {
        holder.setGuideInformation(arrayListGuideInformation[position])
    }

    override fun getItemCount(): Int = arrayListGuideInformation.size
}