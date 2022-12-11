package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.DatesPagerAdapter
import com.example.trip.adapters.DatesPagerClickListener
import com.example.trip.databinding.FragmentOptimalDatesBinding
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.availability.AvailabilityViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OptimalDatesFragment @Inject constructor() : BaseFragment<FragmentOptimalDatesBinding>(),
    DatesPagerClickListener {

    private val viewModel: AvailabilityViewModel by hiltNavGraphViewModels(R.id.availability)

    private lateinit var dateValidator: DateValidator

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var adapter: DatesPagerAdapter

    private val viewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (adapter.currentList.isNotEmpty()) {
                val availability = adapter.currentList[position].availability
                updateCalendar(availability)
            } else {
                resetCalendar()
            }
        }
    }

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOptimalDatesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.secondary) { viewModel.refreshOptimalAvailability() }
        onMyDatesClick()
        setCalendar()
        setPager()
    }

    private fun resetCalendar() {
        dateValidator = DateValidator(listOf())
        binding.calendar.setDayBinder(
            dateValidator,
            R.color.secondary,
            R.color.secondary_transparent
        )
    }

    private fun setPager() {
        binding.pagerDates.adapter = adapter
        adapter.setDatesClickListener(this)
        adapter.showAccept = requireArguments().getLongArray(Constants.COORDINATORS_KEY)!!
            .contains(preferencesHelper.getUserId())
        TabLayoutMediator(binding.tabPager, binding.pagerDates) { _, _ ->
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
        viewModel.optimalAvailability.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    if (it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
                    binding.layoutLoading.setGone()
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                    //binding.layoutLoading.setVisible()
                }
                is Resource.Failure -> {
                    it.message?.let {
                        (requireActivity() as MainActivity).showSnackbar(
                            requireView(),
                            it,
                            R.string.text_retry
                        ) { viewModel.refreshOptimalAvailability() }
                    } ?: (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) { viewModel.refreshOptimalAvailability() }
                    binding.layoutLoading.setGone()
                    binding.layoutRefresh.isRefreshing = false
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
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.updateAcceptedAvailabilityAsync(availability.id).await()) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                    viewModel.refreshOptimalAvailability()
                    requireContext().toast(R.string.text_availability_accepted)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        onAcceptClick(availability)
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        binding.pagerDates.unregisterOnPageChangeCallback(viewPagerCallback)
        super.onDestroyView()
    }

    companion object {
        private const val MONTH_RANGE = 1200L
    }
}