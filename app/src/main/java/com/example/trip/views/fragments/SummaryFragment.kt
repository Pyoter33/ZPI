package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.trip.PreTripDirections
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ParticipantsSummaryAdapter
import com.example.trip.databinding.FragmentSummaryBinding
import com.example.trip.databinding.LayoutPdfBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.SummaryViewModel
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialogClickListener
import com.example.trip.views.dialogs.availability.DeleteAvailabilityDialogClickListener
import com.example.trip.views.dialogs.summary.DeleteAcceptedAccommodationDialog
import com.example.trip.views.dialogs.summary.DeleteAcceptedAvailabilityDialog
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class SummaryFragment @Inject constructor() : Fragment(), DeleteAccommodationDialogClickListener,
    DeleteAvailabilityDialogClickListener {

    private lateinit var binding: FragmentSummaryBinding

    private lateinit var accommodationDialog: DeleteAcceptedAccommodationDialog
    private lateinit var availabilityDialog: DeleteAcceptedAvailabilityDialog
    private lateinit var startCity: String

    @Inject
    lateinit var adapter: ParticipantsSummaryAdapter

    private val viewModel: SummaryViewModel by viewModels()

    private val args: SummaryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        observeAccommodation()
        observeAvailability()
        observeParticipants()
        requireActivity().onBackArrowClick(binding.buttonBack)
        onListLockClick()
        onUncheckDateClick()
        observeButtonLock()
        onUncheckAccommodationClick()
        setupOnDateTextChangeListener()
    }

    private fun setAdapter() {
        binding.listParticipants.adapter = adapter
    }

    private fun onListLockClick() {
        binding.buttonLock.setOnClickListener {
            it.isSelected = !it.isSelected
            binding.listParticipants.isNestedScrollingEnabled = it.isSelected
        }
    }

    private fun onUncheckAccommodationClick() {
        binding.buttonUncheckAccommodation.setOnClickListener {
            accommodationDialog.show(childFragmentManager, DeleteAcceptedAccommodationDialog.TAG)
        }
    }

    private fun onUncheckDateClick() {
        binding.buttonUncheckDates.setOnClickListener {
            availabilityDialog.show(childFragmentManager, DeleteAcceptedAvailabilityDialog.TAG)
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
                        accommodationDialog = DeleteAcceptedAccommodationDialog(this, accommodation)
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
                        availabilityDialog = DeleteAcceptedAvailabilityDialog(this, availability)
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
            textPrice.text = accommodation.price.toStringFormat(args.currency)
            textDescription.text = accommodation.description

            Glide.with(this@SummaryFragment).load(accommodation.imageUrl).centerCrop().into(binding.imageAccommodation)

            buttonLink.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(accommodation.sourceUrl)
                )
                startActivity(intent)
            }

            buttonTransport.setOnClickListener {
                findNavController().navigate(PreTripDirections.actionToTransport(args.groupId, accommodation.id, accommodation.address, startCity, args.currency))
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

    private fun setPdf() {
        val pdfBinding = LayoutPdfBinding.inflate(LayoutInflater.from(context))

        with(pdfBinding) {
            editTextDate.text = binding.editTextDate.text
            textName.text = binding.textName.text
            textAddress.text = binding.textAddress.text
            textVotes.text = binding.textVotes.text
            textPrice.text = binding.textPrice.text
            textDescription.text = binding.textDescription.text
            listParticipants.adapter = adapter
        }
    }





    override fun onDeleteClick(accommodation: Accommodation) {
        lifecycleScope.launch {
            when (viewModel.deleteAcceptedAccommodationAsync(accommodation).await()) {
                is Resource.Success -> {
                    hideAccommodation()
                    viewModel.updateButtonLock(accommodationAdded = false)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onDeleteClick(accommodation)
                    }
                }

            }
        }
    }

    override fun onDeleteClick(availability: Availability) {
        lifecycleScope.launch {
            when (viewModel.deleteAcceptedAvailabilityAsync(availability).await()) {
                is Resource.Success -> {
                    hideDate()
                    viewModel.updateButtonLock(dateAdded = false)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onDeleteClick(availability)
                    }
                }
            }
        }
    }
}