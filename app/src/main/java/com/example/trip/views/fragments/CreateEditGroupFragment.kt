package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.databinding.FragmentCreateEditGroupBinding
import com.example.trip.models.Resource
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.popBackStackWithRefresh
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.viewmodels.groups.CreateEditGroupViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditGroupFragment @Inject constructor() : BaseFragment<FragmentCreateEditGroupBinding>() {

    private val viewModel: CreateEditGroupViewModel by viewModels()

    private val args: CreateEditGroupFragmentArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateEditGroupBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArgs()
        setupOnDescriptionTextChangeListener()
        setupOnCityTextChangeListener()
        setupOnNameTextChangeListener()
        setupOnDaysTextChangeListener()
        setupOnParticipantsTextChangeListener()
        setupSpinner()
        onSubmitClick()
        onBackArrowClick(binding.buttonBack)
    }

    private fun setupArgs() {
        args.group?.let {
            viewModel.toPost = false
            binding.textNewGroup.text = getString(R.string.text_edit_group)
            with(binding) {
                viewModel.name = it.name
                viewModel.currency = it.currency
                viewModel.participants = it.minParticipants.toString()
                viewModel.days = it.minDays.toString()
                viewModel.startingCity = it.startCity
                viewModel.descriptionText = it.description
                editTextName.setText(it.name)
                editTextStartingCity.setText(it.startCity)
                editTextMinParticipants.setText(it.minParticipants.toString())
                editTextMinDays.setText(it.minDays.toString())
                spinnerCurrency.setText(it.currency)
                editTextDescription.setText(it.description)
            }
        }
    }

    private fun setupOnNameTextChangeListener() {
        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.name = editable?.toString()
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

    private fun setupOnCityTextChangeListener() {
        binding.textFieldCity.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.startingCity = editable?.toString()
                binding.textFieldCity.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldCity.error = null
            }

        })
    }

    private fun setupOnParticipantsTextChangeListener() {
        binding.textFieldMinParticipants.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.participants = editable?.toString()
                binding.textFieldMinParticipants.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldMinParticipants.error = null
            }

        })
    }

    private fun setupOnDaysTextChangeListener() {
        binding.textFieldMinDays.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.days = editable?.toString()
                binding.textFieldMinDays.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldMinDays.error = null
            }

        })
    }

    private fun setupSpinner() {
        val items = listOf("USD", "EUR", "PLN", "CZK", "GBP", "HRK", "UAH", "JPY")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, items)
        binding.spinnerCurrency.setAdapter(adapter)

        binding.spinnerCurrency.setOnItemClickListener { _, _, _, _ ->
            viewModel.currency = binding.spinnerCurrency.text.toString()
            binding.textFieldCurrency.startIconDrawable?.setTint(
                resources.getColor(
                    R.color.primary,
                    null
                )
            )
            binding.textFieldCurrency.error = null
        }
    }

    private fun setupOnDescriptionTextChangeListener() {
        binding.textFieldDescription.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.descriptionText = editable?.toString()
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
            if (viewModel.toPost) viewModel.postGroupAsync() else viewModel.updateGroupAsync()

        enableLoading()
        lifecycleScope.launch {
            when (operation.await()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStackWithRefresh()
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    disableLoading()
                    Snackbar.make(
                        requireView(),
                        R.string.text_post_failure,
                        Snackbar.LENGTH_LONG
                    ).setBackgroundTint(resources.getColor(R.color.grey400, null))
                        .setTextColor(resources.getColor(R.color.black, null))
                        .setActionTextColor(resources.getColor(R.color.primary, null))
                        .setAction(R.string.text_retry) {
                            submit()
                        }.show()
                }
            }
        }
    }

    private fun isSubmitNotPermitted(): Boolean {
        var showError = false

        with(binding) {
            if (viewModel.name.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.startingCity.isNullOrEmpty()) {
                textFieldCity.error = getString(R.string.text_text_empty)
                textFieldCity.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.currency.isNullOrEmpty()) {
                textFieldCurrency.error = getString(R.string.text_text_empty)
                textFieldCurrency.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.participants.isNullOrEmpty()) {
                textFieldMinParticipants.error = getString(R.string.text_text_empty)
                textFieldMinParticipants.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.red,
                        null
                    )
                )
                showError = true
            }
            if (viewModel.days.isNullOrEmpty()) {
                textFieldMinDays.error = getString(R.string.text_text_empty)
                textFieldMinDays.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
        }

        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldName.isEnabled = false
            textFieldCity.isEnabled = false
            textFieldCurrency.isEnabled = false
            textFieldDescription.isEnabled = false
            textFieldMinDays.isEnabled = false
            textFieldMinParticipants.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldName.isEnabled = true
            textFieldCity.isEnabled = true
            textFieldCurrency.isEnabled = true
            textFieldDescription.isEnabled = true
            textFieldMinDays.isEnabled = true
            textFieldMinParticipants.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

}