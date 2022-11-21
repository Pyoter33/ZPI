package com.example.trip.views.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.DatesClickListener
import com.example.trip.adapters.DatesExtendedListAdapter
import com.example.trip.databinding.FragmentAvailabilityBinding
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.availability.AvailabilityViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class UserAvailabilityFragment @Inject constructor() : Fragment(), DatesClickListener {

    private val viewModel: AvailabilityViewModel by hiltNavGraphViewModels(R.id.availability)

    private lateinit var binding: FragmentAvailabilityBinding

    private lateinit var dateValidator: DateValidator

    @Inject
    lateinit var adapter: DatesExtendedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAvailabilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        onAddClick()
        onOptimalDatesClick()
        onExpandClick()
        requireActivity().onBackArrowClick(binding.buttonBack)
        setCalendar()
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
                observeAvailabilityList()
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

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setDatesClickListener(this)
        with(binding) {
            listAvailabilities.adapter = adapter
            listAvailabilities.layoutManager = layoutManager
            listAvailabilities.itemAnimator = null
            listAvailabilities.addItemDecoration(
                MaterialDividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                ).apply {
                    isLastItemDecorated = false
                }
            )
        }
    }

    private fun onExpandClick() {
        binding.buttonExpand.setOnClickListener {
            it.isSelected = !it.isSelected
            with(binding) {
                if (it.isSelected) {
                    listAvailabilitiesBackground.animateSlideTransition(
                        Gravity.TOP,
                        layoutTopBackground,
                        ANIM_DURATION
                    )
                    listAvailabilitiesBackground.setVisible()
                    buttonAdd.animateFadeTransition(main, ANIM_DURATION)
                    buttonAdd.setGone()
                } else {
                    listAvailabilitiesBackground.animateSlideTransition(
                        Gravity.TOP,
                        layoutTopBackground,
                        ANIM_DURATION
                    )
                    binding.listAvailabilitiesBackground.setGone()
                    buttonAdd.animateFadeTransition(main, ANIM_DURATION)
                    binding.buttonAdd.setVisible()
                }
            }
        }
    }

    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {
            setAndShowCalendar()
        }
    }

    private fun onOptimalDatesClick() {
        binding.buttonSwitchToOptimalDates.setOnClickListener {
            (requireParentFragment() as AvailabilityPager).switchToOptimalDatesFragment()
        }
    }

    private fun observeAvailabilityList() {
        viewModel.refreshAvailability()
        viewModel.availabilityList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    dateValidator = DateValidator(it.data)
                    updateView(it.data.first())
                    binding.layoutLoading.setGone()
                }
                is Resource.Loading -> {
                    binding.layoutLoading.setVisible()
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refreshAvailability()
                    }
                    binding.layoutLoading.setGone()
                }
            }
        }
    }

    private fun updateView(availability: Availability) {
        with(binding) {
            calendar.scrollToMonth(availability.startDate.yearMonth)
            calendar.setDayBinder(
                dateValidator,
                R.color.primary,
                R.color.primary_transparent
            )
        }
    }

    private fun setAndShowCalendar() {
        val calendarConstraints = CalendarConstraints.Builder().setValidator(dateValidator).build()
        val calendar =
            MaterialDatePicker.Builder.dateRangePicker().setCalendarConstraints(calendarConstraints)
                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()

        calendar.addOnPositiveButtonClickListener {
            addDatesIfCorrect(it.first, it.second)
        }
        calendar.show(childFragmentManager, "DatePicker")
    }

    private fun addDatesIfCorrect(startDate: Long, endDate: Long) {
        if (dateValidator.areDatesCorrect(startDate, endDate)) {
            addDates(startDate, endDate)
        } else {
            requireContext().toast(R.string.text_dates_overlapping)
        }
    }

    private fun addDates(startDate: Long, endDate: Long) {
        val availability = Availability(
            0,
            1,
            startDate.toLocalDate(),
            endDate.toLocalDate()
        )

        lifecycleScope.launch {
            when (viewModel.postAvailability(availability)) {
                is Resource.Success -> {
                    viewModel.refreshAvailability()
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        addDates(startDate, endDate)
                    }
                }
            }
        }
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
        private const val GROUP_ID_ARG = "groupId"
        private const val MONTH_RANGE = 1200L
        private const val ANIM_DURATION = 600L
    }

    override fun onDeleteClick(id: Long) {

    }

}