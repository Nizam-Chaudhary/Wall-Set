package com.nizam.wallset.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nizam.wallset.ui.fragments.CategoriesFragment
import com.nizam.wallset.ui.fragments.FavoriteFragment
import com.nizam.wallset.ui.fragments.SettingsFragment
import com.nizam.wallset.ui.fragments.TopPicksFragment
import com.nizam.wallset.ui.fragments.WallPapersFragment

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CategoriesFragment()
            1 -> TopPicksFragment()
            2 -> WallPapersFragment()
            3 -> FavoriteFragment()
            4 -> SettingsFragment()
            else -> throw (IllegalArgumentException("Invalid Position"))
        }
    }
}