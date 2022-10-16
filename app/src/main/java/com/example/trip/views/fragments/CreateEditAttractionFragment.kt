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
import com.example.trip.databinding.FragmentCreateEditAttractionBinding
import com.example.trip.models.Resource
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toast
import com.example.trip.viewmodels.dayplan.CreateEditAttractionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditAttractionFragment @Inject constructor() : Fragment() {

    private val viewModel: CreateEditAttractionViewModel by viewModels()

    private val args: CreateEditAttractionFragmentArgs by navArgs()

    private lateinit var binding: FragmentCreateEditAttractionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditAttractionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

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
        viewModel.toPost = args.attraction.id == 0
        if(!viewModel.toPost) {
            binding.textNewAttraction.text = getString(R.string.text_edit_attraction)
        }
        binding.editTextName.setText(args.attraction.name)
        binding.editTextName.isEnabled = false
        binding.editTextDescription.setText(args.attraction.description)
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
            val operation = if(viewModel.toPost) viewModel.postAttractionAsync() else viewModel.updateAttractionAsync()

            enableLoading()
            lifecycleScope.launch {
                when (operation.await()) {
                    is Resource.Success -> {
                        disableLoading()
                        findNavController().popBackStack(R.id.attractionsFragment, false)
                    }
                    is Resource.Failure -> {
                        disableLoading()
                        requireContext().toast(getString(R.string.text_item_post_failure, "attraction"))
                    }
                    else -> {
                        //NO-OP
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