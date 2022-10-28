package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.databinding.FragmentCreateEditAccommodationBinding
import com.example.trip.models.Resource
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.viewmodels.accommodation.CreateEditAccommodationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditAccommodationFragment @Inject constructor() : Fragment() {

    private val viewModel: CreateEditAccommodationViewModel by viewModels()

    private val args: CreateEditAccommodationFragmentArgs by navArgs()

    private lateinit var binding: FragmentCreateEditAccommodationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditAccommodationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArgs()
        setupOnDescriptionTextChangeListener()
        setupOnLinkTextChangeListener()
        onSubmitClick()
        onBackArrowClick()
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupArgs() {
        args.link?.let {
            binding.textFieldLink.editText!!.setText(it)
            viewModel.linkText = it
        }
        args.description?.let {
            binding.textFieldDescription.editText!!.setText(it)
            viewModel.descriptionText = it
        }
    }

    private fun setupOnLinkTextChangeListener() {
        binding.textFieldLink.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.linkText = editable?.toString()
                binding.textFieldLink.startIconDrawable?.setTint(resources.getColor(R.color.primary, null))
                binding.textFieldLink.error = null
            }

        })
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

        enableLoading()
        lifecycleScope.launch {
            when (viewModel.postAccommodation()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStack()
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    disableLoading()
                    (requireActivity() as MainActivity).showSnackbar(
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
        val textLink = binding.textFieldLink
        var showError = false

        if (viewModel.linkText.isNullOrEmpty()) {
            textLink.error = getString(R.string.text_text_empty)
            textLink.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
            showError = true
        }

        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldLink.isEnabled = false
            textFieldDescription.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldLink.isEnabled = true
            textFieldDescription.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

}