package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.trip.Constants
import com.example.trip.Constants.SUMMARY_FILE_NAME
import com.example.trip.R
import com.example.trip.activities.HomeActivity
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ParticipantsSummaryAdapter
import com.example.trip.databinding.FragmentSummaryBinding
import com.example.trip.databinding.LayoutPdfBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.SummaryViewModel
import com.example.trip.views.dialogs.TransportDialog
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialogClickListener
import com.example.trip.views.dialogs.availability.DeleteAvailabilityDialogClickListener
import com.example.trip.views.dialogs.summary.DeleteAcceptedAccommodationDialog
import com.example.trip.views.dialogs.summary.DeleteAcceptedAvailabilityDialog
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class SummaryFragment @Inject constructor() : BaseFragment<FragmentSummaryBinding>(),
    DeleteAccommodationDialogClickListener,
    DeleteAvailabilityDialogClickListener {

    private lateinit var accommodationDialog: DeleteAcceptedAccommodationDialog
    private lateinit var availabilityDialog: DeleteAcceptedAvailabilityDialog

    @Inject
    lateinit var adapter: ParticipantsSummaryAdapter

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    private val viewModel: SummaryViewModel by viewModels()

    private val args: SummaryFragmentArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSummaryBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        observeSummary()
        requireActivity().onBackArrowClick(binding.buttonBack)
        onListLockClick()
        onUncheckDateClick()
        onStartTripButtonClick()
        observeButtonLock()
        onUncheckAccommodationClick()
        setupOnDatesClickListener()
        onSaveClick()
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

    private fun setupOnDatesClickListener() {
        binding.editTextDate.setOnClickListener {
            if (!isCoordinator()) {
                requireContext().toast(R.string.text_cannot_modify_dates_coordinator)
                return@setOnClickListener
            }
            setAndShowCalendar()
        }
    }

    private fun observeButtonLock() {
        viewModel.isButtonUnlocked.observe(viewLifecycleOwner) {
            binding.buttonStartTrip.isEnabled = it
        }
    }

    private fun onStartTripButtonClick() {
        if (!isCoordinator()) {
            binding.buttonStartTrip.setGone()
            return
        }
        binding.buttonStartTrip.setOnClickListener {
            startTrip()
        }
    }

    private fun observeSummary() {
        viewModel.summary.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.layoutLoading.setGone()
                    it.data.accommodation?.let { accommodation ->
                        setAccommodation(accommodation)
                        viewModel.updateButtonLock(accommodationAdded = true)
                        accommodationDialog = DeleteAcceptedAccommodationDialog(this, accommodation)
                    } ?: hideAccommodation()

                    it.data.availability?.let { availability ->
                        setDate(availability.availability)
                        viewModel.startDate = availability.availability.startDate
                        viewModel.updateButtonLock(dateAdded = true)
                        availabilityDialog =
                            DeleteAcceptedAvailabilityDialog(this, availability.availability)
                    } ?: hideDate()

                    adapter.submitList(it.data.participants)
                    binding.textParticipantsNo.text = it.data.participants.size.toString()
                }
                is Resource.Loading -> {
                    binding.layoutLoading.setVisible()
                }
                is Resource.Failure -> {
                    binding.layoutLoading.setGone()
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
                    ) {
                        viewModel.refresh()
                    }
                }
            }
        }
    }

    private fun startTrip() {
        binding.layoutLoading.setVisible()
        lifecycleScope.launch {
            when (viewModel.changeGroupStatusAsync().await()) {
                is Resource.Success -> {
                    startActivityWithGroup()
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    binding.layoutLoading.isVisible = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_start_trip_failure,
                        R.string.text_retry
                    ) {
                        startTrip()
                    }
                }

            }
        }
    }

    private fun startActivityWithGroup() {
        lifecycleScope.launch {
            when (val result = viewModel.getGroupAsync().await()) {
                is Resource.Success -> {
                    binding.layoutLoading.setGone()
                    val activityIntent = Intent(requireContext(), HomeActivity::class.java)
                    activityIntent.putExtra(Constants.GROUP_KEY, result.data)
                    startActivity(activityIntent)
                    requireActivity().finish()
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    binding.layoutLoading.setGone()
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_start_trip_failure,
                        R.string.text_retry
                    ) {
                        startTrip()
                    }
                }
            }
        }
    }

    private fun onSaveClick() {
        binding.buttonSave.setOnClickListener {
            setPdf()
        }
    }

    private fun setAndShowCalendar() {
        val calendarConstraints =
            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
        val calendar =
            MaterialDatePicker.Builder.dateRangePicker().setCalendarConstraints(calendarConstraints)
                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()

        calendar.addOnPositiveButtonClickListener {
            addDates(it.first, it.second)
        }
        calendar.show(childFragmentManager, "DatePicker")
    }

    private fun addDates(startDate: Long, endDate: Long) {
        val availability = Availability(
            0,
            -1,
            startDate.toLocalDate(),
            endDate.toLocalDate()
        )

        lifecycleScope.launch {
            when (viewModel.setNewAcceptedAvailabilityAsync(availability).await()) {
                is Resource.Success -> {
                    setDate(availability)
                    availabilityDialog =
                        DeleteAcceptedAvailabilityDialog(this@SummaryFragment, availability)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refresh()
                    }
                }

            }
        }
    }

    private fun isCoordinator() = preferencesHelper.getUserId() in args.coordinators

    private fun setAccommodation(accommodation: Accommodation) {
        with(binding) {
            textAccommodationNotAccepted.setGone()
            cardAccommodation.setVisible()
            imageAccommodationStatus.setVisible()
            if (isCoordinator()) buttonUncheckAccommodation.setVisible()

            textName.text = accommodation.name
            textAddress.text = accommodation.address
            textVotes.text = accommodation.votes.toString()
            textPrice.text = accommodation.price.toStringFormat(args.currency)
            textDescription.text = accommodation.description

            Glide.with(this@SummaryFragment).load(accommodation.imageUrl)
                .placeholder(R.drawable.ic_baseline_downloading_24)
                .error(R.drawable.ic_baseline_question_mark_24).centerCrop()
                .into(binding.imageAccommodation)

            buttonLink.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(accommodation.sourceUrl)
                )
                startActivity(intent)
            }

            buttonTransport.setOnClickListener {
                onTransportClick(accommodation)
            }
        }
    }

    private fun onTransportClick(accommodation: Accommodation) {
        if (viewModel.startDate == null) {
            showTransportDialog()
            return
        }
        navigateToTransport(accommodation, viewModel.startDate!!)
    }

    private fun showTransportDialog() {
        val transportDialog = TransportDialog()
        transportDialog.show(childFragmentManager, TransportDialog.TAG)
    }

    private fun navigateToTransport(accommodation: Accommodation, startDate: LocalDate) {
        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()
        val bundle = Bundle().apply {
            putLong(Constants.GROUP_ID_KEY, accommodation.groupId)
            putLong(Constants.ACCOMMODATION_ID_KEY, accommodation.id)
            putString(Constants.DESTINATION_KEY, accommodation.address)
            putString(Constants.START_CITY_KEY, args.startCity)
            putString(Constants.CURRENCY_KEY, args.currency)
            putLong(Constants.START_DATE_KEY, startDate.toMillis())
            putLong(Constants.ACCOMMODATION_CREATOR_ID_KEY, accommodation.creatorId)
            putLongArray(Constants.COORDINATORS_KEY, args.coordinators)
        }
        findNavController().navigate(R.id.transport, bundle, options)
    }

    private fun hideAccommodation() {
        with(binding) {
            textAccommodationNotAccepted.setVisible()
            cardAccommodation.setGone()
            imageAccommodationStatus.setGone()
            buttonUncheckAccommodation.setGone()
            binding.buttonStartTrip.isEnabled = false
        }
    }

    private fun setDate(availability: Availability) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        with(binding) {
            textDatesNotAccepted.setGone()
            imageDatesStatus.setVisible()
            editTextDate.setText(
                getString(
                    R.string.format_dash,
                    availability.startDate.format(formatter),
                    availability.endDate.format(formatter)
                )
            )
            if (isCoordinator()) buttonUncheckDates.setVisible()
        }
        viewModel.updateButtonLock(dateAdded = true)
    }

    private fun hideDate() {
        with(binding) {
            textDatesNotAccepted.setVisible()
            imageDatesStatus.setGone()
            buttonUncheckDates.setGone()
            editTextDate.setText("")
            binding.buttonStartTrip.isEnabled = false
        }
    }

    private fun setPdf() {
        val pdfBinding = LayoutPdfBinding.inflate(LayoutInflater.from(context))

        with(pdfBinding) {
            editTextDate.text = binding.editTextDate.text
            listParticipants.adapter = adapter
            if (binding.cardAccommodation.isVisible) {
                textName.text = binding.textName.text
                textAddress.text = binding.textAddress.text
                textVotes.text = binding.textVotes.text
                textPrice.text = binding.textPrice.text
                textParticipantsNo.text = binding.textParticipantsNo.text
                imageAccommodation.setImageDrawable(binding.imageAccommodation.drawable)
                textDescription.text = binding.textDescription.text
            } else {
                cardAccommodation.setInvisible()
            }
        }

        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromView(pdfBinding.root)
            .setFileName(SUMMARY_FILE_NAME)
            .build(object : PdfGeneratorListener() {
                override fun onStartPDFGeneration() {
                    //NO-OP
                }

                override fun onFinishPDFGeneration() {
                    //NO-OP
                }

            })

    }

    override fun onDeleteClick(accommodation: Accommodation) {
        lifecycleScope.launch {
            when (viewModel.deleteAcceptedAccommodationAsync().await()) {
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
            when (viewModel.deleteAcceptedAvailabilityAsync().await()) {
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