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
import com.example.trip.R
import com.example.trip.activities.HomeActivity
import com.example.trip.databinding.FragmentEditUserBinding
import com.example.trip.dto.AppUserDto
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.groups.EditUserViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class EditUserFragment @Inject constructor() : BaseFragment<FragmentEditUserBinding>() {

    private val viewModel: EditUserViewModel by viewModels()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditUserBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSubmitClick()
        observeUser()
        onBackArrowClick(binding.buttonBack)
        setupOnNameTextChangeListener()
        setupOnSurnameTextChangeListener()
        setupOnDateTextChangeListener()
        setupOnPhoneTextChangeListener()
        setupOnCodeTextChangeListener()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setValues(it.data)
                    disableLoading()
                }
                is Resource.Loading -> {
                    enableLoading()
                }
                is Resource.Failure -> {
                    (requireActivity() as HomeActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refresh()
                    }
                    disableLoading()
                }
            }
        }
    }

    private fun setValues(appUserDto: AppUserDto) {
        val splitPhone = appUserDto.phoneNumber.split(' ')
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        with(binding) {
            editTextName.setText(appUserDto.firstName)
            editTextSurname.setText(appUserDto.surname)
            editTextDate.setText(appUserDto.surname)
            editTextCode.setText(splitPhone.first().drop(1))
            editTextPhone.setText(splitPhone.last())
            editTextDate.setText(appUserDto.birthday.format(dateFormatter))
        }
        viewModel.appUserDto = appUserDto
        viewModel.firstName = appUserDto.firstName
        viewModel.surname = appUserDto.surname
        viewModel.code = splitPhone.first().drop(1)
        viewModel.phone = splitPhone.last()
        viewModel.birthday = appUserDto.birthday
    }

    private fun setupOnNameTextChangeListener() {
        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.firstName = editable?.toString()
                binding.textFieldName.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldName.error = null
            }
        })
    }

    private fun setupOnSurnameTextChangeListener() {
        binding.textFieldSurname.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.surname = editable?.toString()
                binding.textFieldSurname.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldSurname.error = null
            }
        })
    }

    private fun setupOnCodeTextChangeListener() {
        binding.textFieldCode.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.code = editable?.toString()
                binding.textError.setInvisible()
            }
        })
    }

    private fun setupOnPhoneTextChangeListener() {
        binding.textFieldPhone.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.phone = editable?.toString()
                binding.textFieldPhone.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textError.setInvisible()
            }
        })
    }

    private fun setupOnDateTextChangeListener() {
        binding.editTextDate.setOnClickListener {
            setAndShowCalendar()
        }
    }

    private fun setAndShowCalendar() {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val calendarConstraints = CalendarConstraints.Builder().setValidator(
            DateValidatorPointBackward.now()).build()
        val calendar =
            MaterialDatePicker.Builder.datePicker().setCalendarConstraints(calendarConstraints)
                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()

        calendar.addOnPositiveButtonClickListener {
            val date = it.toLocalDate()
            viewModel.birthday = date
            binding.editTextDate.setText(date.format(dateFormatter))
            binding.textFieldDate.error = null
            binding.textFieldDate.startIconDrawable?.setTint(
                resources.getColor(
                    R.color.primary,
                    null
                )
            )
        }
        calendar.show(childFragmentManager, "DatePicker")
    }

    private fun onSubmitClick() {
        binding.buttonSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if (isSubmitNotPermitted()) return
        enableLoading()

        lifecycleScope.launch {
            when (viewModel.updateUserAsync().await()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStack()
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    disableLoading()
                    (requireActivity() as HomeActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
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
            if (viewModel.firstName.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.surname.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.code.isNullOrEmpty()) {
                textError.setVisible()
                textError.text = getString(R.string.text_error_multiple)
                showError = true
            }
            if (viewModel.phone.isNullOrEmpty()) {
                textError.setVisible()
                textError.text = getString(R.string.text_error_multiple)
                textFieldPhone.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.phone?.length != 9) {
                textError.setVisible()
                textError.text = getString(R.string.text_error_phone)
                textFieldPhone.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.birthday == null) {
                textFieldDate.error = getString(R.string.text_text_empty)
                textFieldDate.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
        }

        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldDate.isEnabled = false
            textFieldPhone.isEnabled = false
            textFieldCode.isEnabled = false
            textFieldName.isEnabled = false
            textFieldSurname.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldDate.isEnabled = true
            textFieldPhone.isEnabled = true
            textFieldCode.isEnabled = true
            textFieldName.isEnabled = true
            textFieldSurname.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

}