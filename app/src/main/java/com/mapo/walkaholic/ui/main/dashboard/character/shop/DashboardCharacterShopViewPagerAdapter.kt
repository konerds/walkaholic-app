package com.mapo.walkaholic.ui.main.dashboard.character.shop

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterShopSlotClickListener

class DashboardCharacterShopViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentSize: Int,
    private val listener: CharacterShopSlotClickListener
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = fragmentSize

    override fun createFragment(position: Int): Fragment {
        return DashboardCharacterShopDetailFragment(position, listener)
    }
}