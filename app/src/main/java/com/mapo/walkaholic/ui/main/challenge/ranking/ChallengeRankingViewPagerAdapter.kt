package com.mapo.walkaholic.ui.main.challenge.ranking

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeDetailRankingFragment

class ChallengeRankingViewPagerAdapter(fragment: BaseFragment<*,*,*>, private val num_pages: Int) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = num_pages

    override fun createFragment(position: Int): Fragment {
        return ChallengeDetailRankingFragment(position)
    }
}