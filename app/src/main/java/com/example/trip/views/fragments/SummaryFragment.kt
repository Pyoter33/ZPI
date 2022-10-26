package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.trip.R
import com.example.trip.adapters.ParticipantsSummaryAdapter
import com.example.trip.databinding.FragmentSummaryBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.SummaryViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class SummaryFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentSummaryBinding

    @Inject
    lateinit var adapter: ParticipantsSummaryAdapter

    private val viewModel: SummaryViewModel by viewModels()

    private var groupId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = getIntFromBundle("groupId")
        setAdapter()
        observeAccommodation()
        observeAvailability()
        observeParticipants()
        onBackArrowClick()
        onUncheckDateClick()
        observeButtonLock()
        onUncheckAccommodationClick()
        setupOnDateTextChangeListener()
    }

    private fun setAdapter() {
        binding.listParticipants.adapter = adapter
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {

        }
    }

    private fun onUncheckAccommodationClick() {
        binding.buttonUncheckAccommodation.setOnClickListener {
            hideAccommodation()
            viewModel.updateButtonLock(accommodationAdded = false)
        }
    }

    private fun onUncheckDateClick() {
        binding.buttonUncheckDates.setOnClickListener {
            hideDate()
            viewModel.updateButtonLock(dateAdded = false)
        }
    }

    private fun setupOnDateTextChangeListener() {
        binding.editTextDate.setOnClickListener {
            setAndShowCalendar()
        }
    }

    private fun observeButtonLock() {
        viewModel.isButtonUnlocked.observe(viewLifecycleOwner) {
            binding.buttonStartTrip.isEnabled = it
        }
    }

    private fun setAndShowCalendar() {
        val calendar =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()

        calendar.addOnPositiveButtonClickListener {
            addDates(it.first, it.second)
        }
        calendar.show(childFragmentManager, "DatePicker")
    }

    private fun addDates(startDate: Long, endDate: Long) {
        val availability = Availability(
            0,
            1,
            startDate.toLocalDate(),
            endDate.toLocalDate()
        )

        lifecycleScope.launch {
            when (viewModel.setNewAcceptedAvailabilityAsync(availability).await()) {
                is Resource.Success -> {
                    setDate(availability)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }

            }
        }
    }

    private fun observeAccommodation() {
        viewModel.acceptedAccommodation.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { accommodation ->
                        setAccommodation(accommodation)
                        viewModel.updateButtonLock(accommodationAdded = true)
                    } ?: hideAccommodation()

                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun observeAvailability() {
        viewModel.acceptedAvailability.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { availability ->
                        setDate(availability)
                        viewModel.updateButtonLock(dateAdded = true)
                    } ?: hideDate()
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun observeParticipants() {
        viewModel.participants.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.textParticipantsNo.text = it.data.size.toString()
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun setAccommodation(accommodation: Accommodation) {
        with(binding) {
            textAccommodationNotAccepted.setGone()
            cardAccommodation.setVisible()
            imageAccommodationStatus.isSelected = true
            buttonUncheckAccommodation.setVisible()

            textName.text = accommodation.name
            textAddress.text = accommodation.address
            textVotes.text = accommodation.votes.toString()
            textPrice.text = getString(R.string.text_pln, accommodation.price.toString())
            textDescription.text = accommodation.description

            buttonLink.setOnClickListener {

            }

            buttonTransport.setOnClickListener {

            }
        }
    }

    private fun hideAccommodation() {
        with(binding) {
            textAccommodationNotAccepted.setVisible()
            cardAccommodation.setGone()
            imageAccommodationStatus.isSelected = false
            buttonUncheckAccommodation.setGone()
            binding.buttonStartTrip.isEnabled = false
        }
    }

    private fun setDate(availability: Availability) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        with(binding) {
            textDatesNotAccepted.setGone()
            imageDatesStatus.isSelected = true
            buttonUncheckDates.setVisible()
            editTextDate.setText(
                getString(
                    R.string.format_dash,
                    availability.startDate.format(formatter),
                    availability.endDate.format(formatter)
                )
            )
        }
        viewModel.updateButtonLock(dateAdded = true)
    }

    private fun hideDate() {
        with(binding) {
            textDatesNotAccepted.setVisible()
            imageDatesStatus.isSelected = false
            buttonUncheckDates.setGone()
            editTextDate.setText("")
            binding.buttonStartTrip.isEnabled = false
        }
    }

}