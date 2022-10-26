package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.trip.R
import com.example.trip.adapters.DatesPagerAdapter
import com.example.trip.adapters.DatesPagerClickListener
import com.example.trip.databinding.FragmentOptimalDatesBinding
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.availability.OptimalDatesViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OptimalDatesFragment @Inject constructor() : Fragment(), DatesPagerClickListener {

    private val viewModel: OptimalDatesViewModel by viewModels()

    private lateinit var binding: FragmentOptimalDatesBinding

    private lateinit var dateValidator: DateValidator

    @Inject
    lateinit var adapter: DatesPagerAdapter

    private val viewPagerCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val availability = adapter.currentList[position].first
            updateCalendar(availability)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptimalDatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onMyDatesClick()
        setCalendar()
        setPager()
    }

    private fun setPager() {
        binding.pagerDates.adapter = adapter
        adapter.setDatesClickListener(this)
        TabLayoutMediator(binding.tabPager, binding.pagerDates) { tab, position ->
        }.attach()

        binding.pagerDates.registerOnPageChangeCallback(viewPagerCallback)
    }

    private fun setCalendar() {
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(MONTH_RANGE)
        val lastMonth = currentMonth.plusMonths(MONTH_RANGE)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        dateValidator = DateValidator(emptyList())
        with(binding) {
            calendar.setupAsync(firstMonth, lastMonth, firstDayOfWeek) {
                calendar.scrollToMonth(currentMonth)
                observeAvailability()
            }
            calendar.setMonthHeaderBinder()
            calendar.onNextMonthClick(buttonNextMonth)
            calendar.onPreviousMonthClick(buttonPreviousMonth)
            calendar.setMonthScrollListener(textCurrentMonth)
            calendar.setDayBinder(
                dateValidator,
                R.color.secondary,
                R.color.secondary_transparent
            )
        }
    }

    private fun observeAvailability() {
        viewModel.availability.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.layoutLoading.setGone()
                }
                is Resource.Loading -> {
                    binding.layoutLoading.setVisible()
                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                    binding.layoutLoading.setGone()
                }
            }
        }
    }

    private fun updateCalendar(availability: Availability) {
        dateValidator = DateValidator(listOf(availability))
        with(binding) {
            calendar.scrollToMonth(availability.startDate.yearMonth)
            calendar.setDayBinder(
                dateValidator,
                R.color.secondary,
                R.color.secondary_transparent
            )
        }
    }

    private fun onMyDatesClick() {
        binding.buttonSwitchToMyDates.setOnClickListener {
            (requireParentFragment() as AvailabilityPager).switchToUserAvailabilityFragment()
        }
    }

    override fun onAcceptClick(availability: Availability) {
        requireActivity().toast("accept")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pagerDates.unregisterOnPageChangeCallback(viewPagerCallback)
    }

    companion object {
        private const val MONTH_RANGE = 1200L
    }
}