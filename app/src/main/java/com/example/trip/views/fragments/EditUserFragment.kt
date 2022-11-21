package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.trip.R
import com.example.trip.databinding.FragmentEditUserBinding
import com.example.trip.utils.setGone
import com.example.trip.utils.setInvisible
import com.example.trip.utils.setVisible
import com.example.trip.utils.toLocalDate
import com.example.trip.viewmodels.groups.EditUserViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class EditUserFragment @Inject constructor(): BaseFragment<FragmentEditUserBinding>() {

    private val viewModel: EditUserViewModel by viewModels()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditUserBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSubmitClick()
        setupOnNameTextChangeListener()
        setupOnDateTextChangeListener()
        setupOnPhoneTextChangeListener()
        setupOnCodeTextChangeListener()
    }

    private fun setupOnNameTextChangeListener() {
        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.fullName = editable?.toString()
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
        val calendar =
            MaterialDatePicker.Builder.datePicker()
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

//        lifecycleScope.launch {
//            when (val result = viewModel.postRegisterAsync().await()) {
//                is Resource.Success -> {
//                    val preferences = requireContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
//                    preferences.edit().putString(Constants.AUTHORIZATION_HEADER, result.data).apply()
//                    requireContext().toast(R.string.text_register_successful)
//                    login()
//                }
//                is Resource.Loading -> {}
//                is Resource.Failure -> {
//                    requireContext().toast(R.string.text_not_register)
//                }
//            }
//
//        }
    }


    private fun isSubmitNotPermitted(): Boolean {
        var showError = false

        with(binding) {
            if(viewModel.fullName.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if(viewModel.code.isNullOrEmpty()) {
                textError.setVisible()
                showError = true
            }
            if(viewModel.phone.isNullOrEmpty()) {
                textError.setVisible()
                textFieldPhone.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if(viewModel.birthday == null) {
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
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldDate.isEnabled = true
            textFieldPhone.isEnabled = true
            textFieldCode.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

}