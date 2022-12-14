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
import com.example.trip.databinding.FragmentCreateEditAttractionBinding
import com.example.trip.models.Resource
import com.example.trip.utils.popBackStackWithRefresh
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.viewmodels.dayplan.CreateEditAttractionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditAttractionFragment @Inject constructor() : BaseFragment<FragmentCreateEditAttractionBinding>() {

    private val viewModel: CreateEditAttractionViewModel by viewModels()

    private val args: CreateEditAttractionFragmentArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateEditAttractionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFields()
        setupOnDescriptionTextChangeListener()
        onSubmitClick()
        onBackArrowClick()
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupFields() {
        args.attraction?.let { attraction ->
            viewModel.toPost = false
            if (!viewModel.toPost) {
                binding.textNewAttraction.text = getString(R.string.text_edit_attraction)
            }
            binding.editTextName.setText(attraction.name)
            binding.editTextName.isEnabled = false
            binding.editTextDescription.setText(attraction.description)
        }

        args.attractionPreview?.let { attraction ->
            viewModel.toPost = true
            if (!viewModel.toPost) {
                binding.textNewAttraction.text = getString(R.string.text_edit_attraction)
            }
            binding.editTextName.setText(attraction.name)
            binding.editTextName.isEnabled = false
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
        val operation = if(viewModel.toPost) viewModel.postAttractionAsync() else viewModel.updateAttractionAsync()

        enableLoading()
        lifecycleScope.launch {
            when (val result = operation.await()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStackWithRefresh(R.id.attractionsFragment, false)
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

    private fun enableLoading() {
        with(binding) {
            textFieldDescription.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldDescription.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

}