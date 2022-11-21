package com.example.trip.views.fragments
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import com.example.trip.Constants
//import com.example.trip.R
//import com.example.trip.activities.HomeActivity
//import com.example.trip.databinding.FragmentEditUserBinding
//import com.example.trip.databinding.FragmentRegisterBinding
//import com.example.trip.models.Resource
//import com.example.trip.utils.*
//import com.example.trip.viewmodels.auth.RegisterViewModel
//import com.google.android.material.datepicker.MaterialDatePicker
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//import java.time.format.DateTimeFormatter
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class EditUserFragment @Inject constructor(): Fragment() {
//
//    private lateinit var binding: FragmentEditUserBinding
//
//    private val viewModel: RegisterViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentEditUserBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        onRegisterClick()
//        setupOnNameTextChangeListener()
//        setupOnRepeatPasswordTextChangeListener()
//        setupOnDateTextChangeListener()
//        setupOnPhoneTextChangeListener()
//        setupOnCodeTextChangeListener()
//        setupOnEmailTextChangeListener()
//        setupOnPasswordTextChangeListener()
//        onSignInClick()
//    }
//
//    private fun setupOnEmailTextChangeListener() {
//        binding.textFieldEmail.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                viewModel.email = editable?.toString()
//                binding.textFieldEmail.startIconDrawable?.setTint(
//                    resources.getColor(
//                        R.color.primary,
//                        null
//                    )
//                )
//                binding.textFieldEmail.error = null
//            }
//        })
//    }
//
//    private fun setupOnNameTextChangeListener() {
//        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                viewModel.fullName = editable?.toString()
//                binding.textFieldName.startIconDrawable?.setTint(
//                    resources.getColor(
//                        R.color.primary,
//                        null
//                    )
//                )
//                binding.textFieldName.error = null
//            }
//        })
//    }
//
//    private fun setupOnCodeTextChangeListener() {
//        binding.textFieldCode.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                viewModel.code = editable?.toString()
//                binding.textError.setInvisible()
//            }
//        })
//    }
//
//    private fun setupOnPhoneTextChangeListener() {
//        binding.textFieldPhone.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                viewModel.phone = editable?.toString()
//                binding.textFieldPhone.startIconDrawable?.setTint(
//                    resources.getColor(
//                        R.color.primary,
//                        null
//                    )
//                )
//                binding.textError.setInvisible()
//            }
//        })
//    }
//
//    private fun setupOnDateTextChangeListener() {
//        binding.editTextDate.setOnClickListener {
//            setAndShowCalendar()
//        }
//    }
//
//    private fun setupOnPasswordTextChangeListener() {
//        binding.textFieldPassword.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                viewModel.password = editable?.toString()
//                binding.textFieldPassword.startIconDrawable?.setTint(
//                    resources.getColor(
//                        R.color.primary,
//                        null
//                    )
//                )
//                binding.textFieldPassword.error = null
//            }
//        })
//    }
//
//    private fun setupOnRepeatPasswordTextChangeListener() {
//        binding.textFieldRepeatPassword.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                viewModel.repeatPassword = editable?.toString()
//                binding.textFieldRepeatPassword.startIconDrawable?.setTint(
//                    resources.getColor(
//                        R.color.primary,
//                        null
//                    )
//                )
//                binding.textFieldRepeatPassword.error = null
//            }
//        })
//    }
//
//    private fun setAndShowCalendar() {
//        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
//        val calendar =
//            MaterialDatePicker.Builder.datePicker()
//                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()
//
//        calendar.addOnPositiveButtonClickListener {
//            val date = it.toLocalDate()
//            viewModel.birthday = date
//            binding.editTextDate.setText(date.format(dateFormatter))
//            binding.textFieldDate.error = null
//            binding.textFieldDate.startIconDrawable?.setTint(
//                resources.getColor(
//                    R.color.primary,
//                    null
//                )
//            )
//        }
//        calendar.show(childFragmentManager, "DatePicker")
//    }
//
//
//    private fun onSignInClick() {
//        binding.buttonSignUp.setOnClickListener {
//            submit()
//        }
//    }
//
//    private fun onRegisterClick() {
//        binding.buttonLogin.setOnClickListener {
//            findNavController().popBackStack()
//        }
//    }
//
//    private fun submit() {
//        if (isSubmitNotPermitted()) return
//        enableLoading()
//
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
//    }
//
//    private fun login() {
//        lifecycleScope.launch {
//            when (val result = viewModel.postLoginAsync().await()) {
//                is Resource.Success -> {
//                    disableLoading()
//                    val preferences = requireContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
//                    preferences.edit().putLong(Constants.USER_ID_KEY, result.data.first.userId).apply()
//                    preferences.edit().putString(Constants.AUTHORIZATION_HEADER, result.data.second).apply()
//                    val activityIntent = Intent(requireContext(), HomeActivity::class.java)
//                    startActivity(activityIntent)
//                }
//                is Resource.Loading -> {}
//                is Resource.Failure -> {
//                    disableLoading()
//                    requireContext().toast(R.string.text_not_login)
//                }
//            }
//
//        }
//    }
//
//
//    private fun isSubmitNotPermitted(): Boolean {
//        var showError = false
//
//        with(binding) {
//            if (viewModel.email.isNullOrEmpty()) {
//                textFieldEmail.error = getString(R.string.text_text_empty)
//                textFieldEmail.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
//                showError = true
//            }
//            if(viewModel.fullName.isNullOrEmpty()) {
//                textFieldName.error = getString(R.string.text_text_empty)
//                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
//                showError = true
//            }
//            if(viewModel.code.isNullOrEmpty()) {
//                textError.setVisible()
//                showError = true
//            }
//            if(viewModel.phone.isNullOrEmpty()) {
//                textError.setVisible()
//                textFieldPhone.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
//                showError = true
//            }
//            if(viewModel.birthday == null) {
//                textFieldDate.error = getString(R.string.text_text_empty)
//                textFieldDate.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
//                showError = true
//            }
//            if (viewModel.password.isNullOrEmpty()) {
//                textFieldPassword.error = getString(R.string.text_text_empty)
//                textFieldPassword.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
//                showError = true
//            }
//            if (viewModel.password != viewModel.repeatPassword) {
//                textFieldRepeatPassword.error = getString(R.string.text_error_not_same)
//                textFieldRepeatPassword.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
//                showError = true
//            }
//        }
//
//        return showError
//    }
//
//    private fun enableLoading() {
//        with(binding) {
//            textFieldEmail.isEnabled = false
//            textFieldDate.isEnabled = false
//            textFieldPhone.isEnabled = false
//            textFieldCode.isEnabled = false
//            textFieldRepeatPassword.isEnabled = false
//            textFieldPassword.isEnabled = false
//            buttonSignUp.isEnabled = false
//            layoutLoading.setVisible()
//        }
//    }
//
//    private fun disableLoading() {
//        with(binding) {
//            textFieldEmail.isEnabled = true
//            textFieldDate.isEnabled = true
//            textFieldPhone.isEnabled = true
//            textFieldCode.isEnabled = true
//            textFieldRepeatPassword.isEnabled = true
//            textFieldPassword.isEnabled = true
//            buttonSignUp.isEnabled = true
//            layoutLoading.setGone()
//        }
//    }
//
//}