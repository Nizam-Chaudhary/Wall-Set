package com.nizam.wallset.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nizam.wallset.ui.fragments.CategoriesFragment
import com.nizam.wallset.ui.fragments.TopPicksFragment

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CategoriesFragment()
            1 -> TopPicksFragment()
            else -> throw(IllegalArgumentException("Invalid Position"))
        }
    }
}