package com.mapo.walkaholic.ui.favoritepath

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mapo.walkaholic.ui.base.BaseSharedFragment

class FavoritePathViewPagerAdapter(fragment: BaseSharedFragment<*, *, *>, private val num_pages: Int) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = num_pages

    override fun createFragment(position: Int): Fragment {
        return FavoritePathDetailFragment(position)
    }
}