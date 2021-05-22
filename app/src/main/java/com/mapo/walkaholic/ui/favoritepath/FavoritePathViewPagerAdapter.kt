package com.mapo.walkaholic.ui.favoritepath

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FavoritePathViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentSize: Int,
    private val listener: FavoritePathClickListener
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = fragmentSize

    override fun createFragment(position: Int): Fragment {
        return FavoritePathDetailFragment(position, listener)
    }
}