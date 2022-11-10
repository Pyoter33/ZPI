package com.example.trip.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = mutableListOf<Fragment>()

    fun addNewFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    companion object {
        private const val NUM_PAGES = 2
    }
}
