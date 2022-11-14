package com.example.trip.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FinancesPagerAdapter(
    private val fragmentsList: List<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val pagerArgs: Bundle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return fragmentsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position].apply { arguments = pagerArgs }
    }

}