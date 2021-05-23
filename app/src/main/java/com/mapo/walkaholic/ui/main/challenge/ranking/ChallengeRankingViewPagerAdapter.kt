package com.mapo.walkaholic.ui.main.challenge.ranking

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChallengeRankingViewPagerAdapter(fragmentManager: FragmentManager,
                                       lifecycle: Lifecycle,
                                       private val fragmentSize: Int) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = fragmentSize

    override fun createFragment(position: Int): Fragment {
        return ChallengeDetailRankingFragment(position)
    }
}