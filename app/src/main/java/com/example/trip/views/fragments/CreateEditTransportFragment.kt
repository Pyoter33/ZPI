package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.databinding.FragmentCreateEditTransportBinding
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.transport.CreateEditTransportViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditTransportFragment @Inject constructor() : BaseFragment<FragmentCreateEditTransportBinding>() {

    private val viewModel: CreateEditTransportViewModel by viewModels()

    private val args: CreateEditTransportFragmentArgs by navArgs()

    private lateinit var dateFormatter: DateTimeFormatter

    private lateinit var timeFormatter: DateTimeFormatter

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateEditTransportBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        setupArgs()
        setupOnTransportTextChangeListener()
        setupOnDestinationTextChangeListener()
        setupOnMeetingLocationTextChangeListener()
        setupOnHoursTextChangeListener()
        setupOnMinutesTextChangeListener()
        setupOnTimeTextChangeListener()
        setupOnDateTextChangeListener()
        setupOnDescriptionTextChangeListener()
        setupOnPriceTextChangeListener()
        onSubmitClick()
        onBackArrowClick(binding.buttonBack)
    }

    private fun setupArgs() {
        args.userTransport?.let {
            viewModel.toPost = false
            binding.textNewTransport.text = getString(R.string.text_edit_transport)
            with(binding) {
                editTextTransports.setText(viewModel.meansOfTransport)
                editTextMeetingLocation.setText(viewModel.meetingLocation)
                editTextDestination.setText(viewModel.destination)
                editTextMinutes.setText(viewModel.durationMinutes)
                editTextHours.setText(viewModel.durationHours)
                editTextMeetingDate.setText(viewModel.meetingDate?.format(dateFormatter))
                editTextMeetingTime.setText(viewModel.meetingTime?.format(timeFormatter))
                editTextPrice.setText(viewModel.price)
                editTextDescription.setText(viewModel.description)
            }
        }
    }

    private fun setupOnTransportTextChangeListener() {
        binding.textFieldTransports.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.meansOfTransport = editable?.toString()
                binding.textFieldTransports.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldTransports.error = null
            }

        })
    }

    private fun setupOnMeetingLocationTextChangeListener() {
        binding.textFieldMeetingLocation.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.meetingLocation = editable?.toString()
                binding.textFieldMeetingLocation.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldMeetingLocation.error = null
            }

        })
    }

    private fun setupOnDestinationTextChangeListener() {
        binding.textFieldDestination.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.destination = editable?.toString()
                binding.textFieldDestination.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldDestination.error = null
            }

        })
    }

    private fun setupOnHoursTextChangeListener() {
        binding.textFieldDurationHours.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.durationHours = editable?.toString()
                binding.textFieldDurationHours.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldDurationHours.error = null
            }

        })
    }

    private fun setupOnMinutesTextChangeListener() {
        binding.textFieldDurationMinutes.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.durationMinutes = editable?.toString()
                 if(!editable.isNullOrEmpty()) {
                    val value = editable.toString().toInt()
                    if (value > MAX_MINUTES) {
                        viewModel.durationMinutes = MAX_MINUTES.toString()
                        binding.editTextMinutes.setText(MAX_MINUTES.toString())
                    } else if (value < MIN_MINUTES) {
                        viewModel.durationMinutes = MIN_MINUTES.toString()
                        binding.editTextMinutes.setText(MIN_MINUTES.toString())
                    }
                }

                binding.textFieldDurationMinutes.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldDurationMinutes.error = null
            }

        })
    }

    private fun setupOnDateTextChangeListener() {
        binding.editTextMeetingDate.setOnClickListener {
            setAndShowCalendar()
        }
    }

    private fun setupOnTimeTextChangeListener() {
        binding.editTextMeetingTime.setOnClickListener {
            setAndShowTime()
        }
    }

    private fun setAndShowCalendar() {
        val calendarConstraints = CalendarConstraints.Builder().setValidator(
            DateValidatorPointBackward.before(args.startDate)).build()
        val calendar =
            MaterialDatePicker.Builder.datePicker().setCalendarConstraints(calendarConstraints)
                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()

        calendar.addOnPositiveButtonClickListener {
            val date = it.toLocalDate()
            viewModel.meetingDate = date
            binding.editTextMeetingDate.setText(date.format(dateFormatter))
            binding.textFieldMeetingDate.error = null
            binding.textFieldMeetingDate.startIconDrawable?.setTint(
                resources.getColor(
                    R.color.primary,
                    null
                )
            )
        }
        calendar.show(childFragmentManager, "DatePicker")
    }

    private fun setAndShowTime() {
        val timer =
            MaterialTimePicker.Builder()
                .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_TimePicker).build()

        timer.addOnPositiveButtonClickListener {
            val time = LocalTime.of(timer.hour, timer.minute)
            viewModel.meetingTime = time
            binding.editTextMeetingTime.setText(time.format(timeFormatter))
            binding.textFieldMeetingTime.error = null
            binding.textFieldMeetingTime.startIconDrawable?.setTint(
                resources.getColor(
                    R.color.primary,
                    null
                )
            )
        }
        timer.show(childFragmentManager, "TimePicker")
    }

    private fun setupOnPriceTextChangeListener() {
        binding.textFieldPrice.suffixText = args.currency
        binding.textFieldPrice.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.price = editable?.toString()
                binding.textFieldPrice.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldPrice.error = null
            }
        })
    }

    private fun setupOnDescriptionTextChangeListener() {
        binding.textFieldDescription.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.description = editable?.toString()
                binding.textFieldDescription.error = null
            }

        })
    }

    private fun onSubmitClick() {
        binding.buttonSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if (isSubmitNotPermitted()) return
        val operation =
            if (viewModel.toPost) viewModel.postTransportAsync() else viewModel.updateTransportAsync()

        enableLoading()
        lifecycleScope.launch {
            when (val result = operation.await()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStackWithRefresh()
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    disableLoading()
                    result.message?.let {
                        (requireActivity() as MainActivity).showSnackbar(
                            requireView(),
                            it,
                            R.string.text_retry
                        ) {
                            submit()
                        }
                    } ?: (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        submit()
                    }
                }
            }
        }
    }

    private fun isSubmitNotPermitted(): Boolean {
        var showError = false

        with(binding) {
            if (viewModel.meansOfTransport.isNullOrEmpty()) {
                textFieldTransports.error = getString(R.string.text_text_empty)
                textFieldTransports.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.meetingLocation.isNullOrEmpty()) {
                textFieldMeetingLocation.error = getString(R.string.text_text_empty)
                textFieldMeetingLocation.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.destination.isNullOrEmpty()) {
                textFieldDestination.error = getString(R.string.text_text_empty)
                textFieldDestination.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.durationHours.isNullOrEmpty()) {
                textFieldDurationHours.error = getString(R.string.text_text_empty)
                showError = true
            }
            if (viewModel.durationMinutes.isNullOrEmpty()) {
                textFieldDurationMinutes.error = getString(R.string.text_text_empty)
                showError = true
            }
            if (viewModel.meetingDate == null) {
                textFieldMeetingDate.error = getString(R.string.text_text_empty)
                textFieldMeetingDate.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.meetingTime == null) {
                textFieldMeetingTime.error = getString(R.string.text_text_empty)
                textFieldMeetingTime.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.price.isNullOrEmpty()) {
                textFieldPrice.error = getString(R.string.text_text_empty)
                textFieldPrice.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }

        }
        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldTransports.isEnabled = false
            textFieldMeetingLocation.isEnabled = false
            textFieldDestination.isEnabled = false
            textFieldDurationHours.isEnabled = false
            textFieldDurationMinutes.isEnabled = false
            textFieldMeetingDate.isEnabled = false
            textFieldMeetingTime.isEnabled = false
            textFieldPrice.isEnabled = false
            textFieldDescription.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldTransports.isEnabled = true
            textFieldMeetingLocation.isEnabled = true
            textFieldDestination.isEnabled = true
            textFieldDurationHours.isEnabled = true
            textFieldDurationMinutes.isEnabled = true
            textFieldMeetingDate.isEnabled = true
            textFieldMeetingTime.isEnabled = true
            textFieldPrice.isEnabled = true
            textFieldDescription.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

    companion object {
        const val MIN_MINUTES = 0
        const val MAX_MINUTES = 59
    }

}
