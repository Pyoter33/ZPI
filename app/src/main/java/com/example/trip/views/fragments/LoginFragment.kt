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
import com.example.trip.databinding.FragmentLoginBinding
import com.example.trip.models.Resource
import com.example.trip.utils.SharedPreferencesHelper
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toast
import com.example.trip.viewmodels.auth.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor(): BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkTokenAndId()
        onRegisterClick()
        setupOnEmailTextChangeListener()
        setupOnPasswordTextChangeListener()
        onSignInClick()
    }

    private fun checkTokenAndId() {
        if(preferencesHelper.containsKey(Constants.USER_ID_KEY) && preferencesHelper.containsKey(Constants.AUTHORIZATION_HEADER)) {
            login()
        }
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

    private fun onSignInClick() {
        binding.buttonSignIn.setOnClickListener {
            submit()
        }
    }

    private fun onRegisterClick() {
        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun submit() {
        if (isSubmitNotPermitted()) return
        enableLoading()

        lifecycleScope.launch {
            when (val result = viewModel.postLoginAsync().await()) {
                is Resource.Success -> {
                    disableLoading()
                    preferencesHelper.setToken(result.data.second)
                    preferencesHelper.setUserId(result.data.first.userId)
                    login()
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    disableLoading()
                    requireContext().toast(R.string.text_not_login)
                }
            }

        }
    }

    private fun login() {
        val activityIntent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(activityIntent)
    }

    private fun isSubmitNotPermitted(): Boolean {
        var showError = false

        with(binding) {
            if (viewModel.email.isNullOrEmpty()) {
                textFieldEmail.error = getString(R.string.text_text_empty)
                textFieldEmail.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.password.isNullOrEmpty()) {
                textFieldPassword.error = getString(R.string.text_text_empty)
                textFieldPassword.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
        }

        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldEmail.isEnabled = false
            textFieldPassword.isEnabled = false
            buttonSignIn.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldEmail.isEnabled = true
            textFieldPassword.isEnabled = true
            buttonSignIn.isEnabled = true
            layoutLoading.setGone()
        }
    }

}