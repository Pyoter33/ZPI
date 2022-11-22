package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.trip.Constants
import com.example.trip.adapters.FragmentPagerAdapter
import com.example.trip.databinding.FragmentAvailabilityPagerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AvailabilityPager @Inject constructor() : BaseFragment<FragmentAvailabilityPagerBinding>() {

    val args: AvailabilityPagerArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAvailabilityPagerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    fun switchToUserAvailabilityFragment() {
        binding.viewPager.setCurrentItem(USER_AVAILABILITY_FRAGMENT_ID, true)
    }

    fun switchToOptimalDatesFragment() {
        binding.viewPager.setCurrentItem(OPTIMAL_DATES_FRAGMENT_ID, true)
    }

    private fun setupViewPager() {
        val adapter = FragmentPagerAdapter(
            this,
            Bundle().apply { putLongArray(Constants.COORDINATORS_KEY, args.coordinators) })
        adapter.apply {
            addNewFragment(UserAvailabilityFragment())
            addNewFragment(OptimalDatesFragment())
        }
        binding.viewPager.adapter = adapter
    }

    companion object {
        private const val USER_AVAILABILITY_FRAGMENT_ID = 0
        private const val OPTIMAL_DATES_FRAGMENT_ID = 1
    }

}