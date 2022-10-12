package com.example.trip.views.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.adapters.AttractionPreviewAdapter
import com.example.trip.adapters.AttractionPreviewClickListener
import com.example.trip.databinding.FragmentFindAttractionBinding
import com.example.trip.models.AttractionPreview
import com.example.trip.models.Resource
import com.example.trip.utils.toAttraction
import com.example.trip.utils.toast
import com.example.trip.viewmodels.dayplan.FindAttractionViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FindAttractionFragment @Inject constructor() : Fragment(), AttractionPreviewClickListener {

    private lateinit var binding: FragmentFindAttractionBinding

    @Inject
    lateinit var adapter: AttractionPreviewAdapter

    private val args: FindAttractionFragmentArgs by navArgs()

    private val viewModel: FindAttractionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindAttractionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        observeAccommodationPreviews()
        setOnSearchClickListener()
        onBackArrowClick()
    }

    private fun setAdapter() {
        binding.listAttractionsPreviews.adapter = adapter
        adapter.setAttractionClickListener(this)
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeAccommodationPreviews() {
        viewModel.attractionsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun setOnSearchClickListener() {
        binding.editTextQuery.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.getData(view.text.toString())
                val imm =
                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onClick(attractionPreview: AttractionPreview) {
        findNavController().navigate(
            FindAttractionFragmentDirections.actionFindAttractionFragmentToCreateEditAttractionFragment(
                attractionPreview.toAttraction(args.groupId, args.dayPlanId)
            )
        )
    }
}