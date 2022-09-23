package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.trip.R
import com.example.trip.databinding.FragmentOptimalDatesBinding
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.availability.OptimalDatesViewModel
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OptimalDatesFragment @Inject constructor() : Fragment() {

    private val viewModel: OptimalDatesViewModel by viewModels()

    private lateinit var binding: FragmentOptimalDatesBinding

    private lateinit var dateValidator: DateValidator

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
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.secondary) { viewModel.refreshData() }
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
        viewModel.refreshData()
        viewModel.availability.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    dateValidator = DateValidator(listOf(it.data.first))
                    updateView(it.data.first, it.data.second)
                    binding.layoutRefresh.isRefreshing = false
                    binding.layoutLoading.setGone()
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                    binding.layoutRefresh.isRefreshing = false
                    binding.layoutLoading.setGone()
                }
            }
        }
    }

    private fun updateView(availability: Availability, participantsNo: Int) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        with(binding) {
            textDateStart.text = availability.startDate.format(formatter)
            textDateEnd.text = availability.endDate.format(formatter)
            textParticipantsNo.text = getString(R.string.text_participants_no, participantsNo)
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

    companion object {
        private const val MONTH_RANGE = 1200L
    }

}