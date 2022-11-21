package com.example.trip.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.AttractionPreviewAdapter
import com.example.trip.adapters.AttractionPreviewClickListener
import com.example.trip.databinding.FragmentFindAttractionBinding
import com.example.trip.models.AttractionPreview
import com.example.trip.models.Resource
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toAttraction
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
                    if(it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
                    adapter.submitList(it.data)
                    binding.layoutLoading.setGone()
                }
                is Resource.Loading -> {
                    binding.layoutLoading.setVisible()
                    binding.textEmptyList.setGone()
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.getData(binding.editTextQuery.text.toString())
                    }
                    binding.layoutLoading.setGone()
                    binding.textEmptyList.setGone()
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

    override fun onSeeMoreClick(attraction: AttractionPreview) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(attraction.link)
        )
        startActivity(intent)
    }
}