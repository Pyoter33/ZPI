package com.example.trip.views.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.HomeActivity
import com.example.trip.databinding.FragmentRegisterBinding
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.auth.RegisterViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment @Inject constructor(): BaseFragment<FragmentRegisterBinding>() {

    private val viewModel: RegisterViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onRegisterClick()
        setupOnNameTextChangeListener()
        setupOnRepeatPasswordTextChangeListener()
        setupOnDateTextChangeListener()
        setupOnPhoneTextChangeListener()
        setupOnCodeTextChangeListener()
        setupOnSurnameTextChangeListener()
        setupOnEmailTextChangeListener()
        setupOnPasswordTextChangeListener()
        onSignInClick()
    }

    private fun setupOnEmailTextChangeListener() {
        binding.textFieldEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.email = editable?.toString()
                binding.textFieldEmail.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldEmail.error = null
            }
        })
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
                binding.textLabelPhone.setTextColor(resources.getColor(R.color.grey700, null))
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
                binding.textLabelPhone.setTextColor(resources.getColor(R.color.grey700, null))
            }
        })
    }

    private fun setupOnDateTextChangeListener() {
        binding.editTextDate.setOnClickListener {
            setAndShowCalendar()
        }
    }

    private fun setupOnPasswordTextChangeListener() {
        binding.textFieldPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.password = editable?.toString()
                binding.textFieldPassword.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldPassword.error = null
            }
        })
    }

    private fun setupOnRepeatPasswordTextChangeListener() {
        binding.textFieldRepeatPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.repeatPassword = editable?.toString()
                binding.textFieldRepeatPassword.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldRepeatPassword.error = null
            }
        })
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


    private fun onSignInClick() {
        binding.buttonSignUp.setOnClickListener {
            submit()
        }
    }

    private fun onRegisterClick() {
        binding.buttonLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun submit() {
        if (isSubmitNotPermitted()) return
        enableLoading()

        lifecycleScope.launch {
            when (val result = viewModel.postRegisterAsync().await()) {
                is Resource.Success -> {
                    requireContext().toast(R.string.text_register_successful)
                    login()
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    disableLoading()
                    result.message?.let {
                        requireContext().toast(it)
                    } ?: requireContext().toast(R.string.text_not_register)
                }
            }
        }
    }

    private fun login() {
        lifecycleScope.launch {
            when (val result = viewModel.postLoginAsync().await()) {
                is Resource.Success -> {
                    disableLoading()
                    preferencesHelper.setToken(result.data.second)
                    preferencesHelper.setUserId(result.data.first.userId)
                    val activityIntent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(activityIntent)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    disableLoading()
                    requireContext().toast(R.string.text_not_login)
                }
            }
        }
    }

    private fun isSubmitNotPermitted(): Boolean {
        var showError = false
        var phoneError = false
        var codeError = false

        with(binding) {
            if (viewModel.email.isNullOrEmpty()) {
                textFieldEmail.error = getString(R.string.text_text_empty)
                textFieldEmail.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if(viewModel.firstName.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if(viewModel.surname.isNullOrEmpty()) {
                textFieldSurname.error = getString(R.string.text_text_empty)
                textFieldSurname.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if(viewModel.code.isNullOrEmpty() || viewModel.code?.length !in 1..4) {
                textError.setVisible()
                textError.text = getString(R.string.text_error_code) //?
                textLabelPhone.setTextColor(resources.getColor(R.color.red, null))
                showError = true
                codeError = true
            }
            if (viewModel.phone.isNullOrEmpty() || viewModel.phone?.length !in 5..13) {
                textError.setVisible()
                textError.text = getString(R.string.text_error_phone)
                textFieldPhone.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                textLabelPhone.setTextColor(resources.getColor(R.color.red, null))
                showError = true
                phoneError = true
            }
            if(codeError && phoneError) {
                textError.setVisible()
                textError.text = getString(R.string.text_error_code_phone)
                showError = true
            }
            if(viewModel.birthday == null) {
                textFieldDate.error = getString(R.string.text_text_empty)
                textFieldDate.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.password.isNullOrEmpty()) {
                textFieldPassword.error = getString(R.string.text_text_empty)
                textFieldPassword.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.password?.matches(Constants.PASSWORD_REGEX.toRegex()) == false) {
                textFieldPassword.error = getString(R.string.text_password_rules)
                textFieldPassword.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.password != viewModel.repeatPassword) {
                textFieldRepeatPassword.error = getString(R.string.text_error_not_same)
                textFieldRepeatPassword.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
        }

        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldEmail.isEnabled = false
            textFieldName.isEnabled = false
            textFieldSurname.isEnabled = false
            textFieldDate.isEnabled = false
            textFieldPhone.isEnabled = false
            textFieldCode.isEnabled = false
            textFieldRepeatPassword.isEnabled = false
            textFieldPassword.isEnabled = false
            buttonSignUp.isEnabled = false
            buttonLogin.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldEmail.isEnabled = true
            textFieldName.isEnabled = true
            textFieldSurname.isEnabled = true
            textFieldDate.isEnabled = true
            textFieldPhone.isEnabled = true
            textFieldCode.isEnabled = true
            textFieldRepeatPassword.isEnabled = true
            textFieldPassword.isEnabled = true
            buttonSignUp.isEnabled = true
            buttonLogin.isEnabled = true
            layoutLoading.setGone()
        }
    }

}