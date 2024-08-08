package com.froztlass.airquality.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 08/08/2024
    // Tugas Besar UAS
    list: ArrayList<Fragment>,
    fm : FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fm, lifecycle) {

    val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}