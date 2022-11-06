package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trip.adapters.AvailabilityPagerAdapter
import com.example.trip.databinding.FragmentFinancesPagerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FinancesPager @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentFinancesPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinancesPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    fun switchToUserAvailabilityFragment() {
        binding.viewPager.setCurrentItem(EXPENSES_FRAGMENT_ID, true)
    }

    fun switchToOptimalDatesFragment() {
        binding.viewPager.setCurrentItem(BALANCE_FRAGMENT_ID, true)
    }

    private fun setupViewPager() {
        val adapter = AvailabilityPagerAdapter(this)
        adapter.apply {
            addNewFragment(UserAvailabilityFragment())
            addNewFragment(OptimalDatesFragment())
        }
        binding.viewPager.adapter = adapter
    }

    companion object {
        private const val EXPENSES_FRAGMENT_ID = 0
        private const val BALANCE_FRAGMENT_ID = 1
    }

}