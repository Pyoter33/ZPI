package com.example.trip.views.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.DatesListAdapter
import com.example.trip.databinding.FragmentParticipantsAvailabilityBinding
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.participants.ParticipantsAvailabilityViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ParticipantsAvailabilityFragment @Inject constructor() : Fragment() {

    private val viewModel: ParticipantsAvailabilityViewModel by viewModels()

    private lateinit var binding: FragmentParticipantsAvailabilityBinding

    private lateinit var dateValidator: DateValidator

    private val args: ParticipantsAvailabilityFragmentArgs by navArgs()

    @Inject
    lateinit var adapter: DatesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParticipantsAvailabilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCalendar()
        onBackArrowClick(binding.buttonBack)
        setAdapter()
        setupArgs()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        onExpandClick()
    }

    private fun setupArgs() {
        binding.textParticipantName.text = args.participant.fullName
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
                R.color.primary,
                R.color.primary_transparent
            )
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
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
                } else {
                    listAvailabilitiesBackground.animateSlideTransition(
                        Gravity.TOP,
                        layoutTopBackground,
                        ANIM_DURATION
                    )
                    binding.listAvailabilitiesBackground.setGone()
                }
            }
        }
    }

    private fun observeAvailabilityList() {
        viewModel.refreshData()
        viewModel.availabilityList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    dateValidator = DateValidator(it.data)
                    updateView(it.data.first())
                    binding.layoutRefresh.isRefreshing = false
                    binding.layoutLoading.setGone()
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refreshData()
                    }
                    binding.layoutRefresh.isRefreshing = false
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

    companion object {
        private const val PLACEHOLDER_USERID = 1
        private const val GROUP_ID_ARG = "groupId"
        private const val MONTH_RANGE = 1200L
        private const val ANIM_DURATION = 600L
    }

}