package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.AttractionClickListener
import com.example.trip.adapters.AttractionListAdapter
import com.example.trip.databinding.FragmentAttractionsBinding
import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.dayplan.AttractionsViewModel
import com.example.trip.views.dialogs.MenuPopupSetStartFactory
import com.example.trip.views.dialogs.dayplan.DeleteAttractionDialog
import com.example.trip.views.dialogs.dayplan.DeleteAttractionDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AttractionsFragment @Inject constructor() : BaseFragment<FragmentAttractionsBinding>(),
    AttractionClickListener,
    DeleteAttractionDialogClickListener {

    private val popupMenu by balloon<MenuPopupSetStartFactory>()

    private val args: AttractionsFragmentArgs by navArgs()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var adapter: AttractionListAdapter

    private val viewModel: AttractionsViewModel by viewModels()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAttractionsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setupArgs()
        observeAccommodationsList()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refresh() }
        onAddClick()
        onBackArrowClick(binding.buttonBack)
        refreshIfNewData { viewModel.refresh() }
    }

    private fun setupArgs() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        binding.textHeader.text = args.dayPlan.name
        binding.textHeaderDate.text = args.dayPlan.date.format(formatter)
    }

    private fun observeAccommodationsList() {
        viewModel.attractionsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
                    adapter.submitList(it.data)
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                    binding.textEmptyList.setGone()
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refresh()
                    }
                    binding.layoutRefresh.isRefreshing = false
                    binding.textEmptyList.setGone()
                }
            }
        }
    }

    private fun setAdapter() {
        adapter.setAttractionClickListener(this)
        binding.attractionsList.adapter = adapter
        binding.attractionsList.layoutManager = LinearLayoutManager(context)
    }

    private fun onAddClick() {
        if(!isCoordinator()) {
            binding.buttonAdd.setGone()
            return
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                AttractionsFragmentDirections.actionAttractionsFragmentToFindAttractionFragment(
                    args.groupId,
                    args.dayPlan.id
                )
            )
        }
    }

    //list item
    override fun onExpandClick(position: Int) {
        viewModel.setExpanded(position)
        adapter.notifyItemChanged(position)
    }

    override fun onSeeMoreClick(link: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )
        startActivity(intent)
    }

    override fun onLongClick(attraction: Attraction, view: View) {
        if (isCoordinator()) {
            popupMenu.setOnPopupButtonClick(R.id.button_edit) {
                onMenuEditClick(attraction)
            }
            popupMenu.setOnPopupButtonClick(R.id.button_delete) {
                onMenuDeleteClick(attraction)
            }
            popupMenu.setOnPopupButtonClick(R.id.button_set_start) {
                onMenuSetStartClick(attraction)
            }
            popupMenu.showAlignBottom(view)
        }
    }

    private fun isCoordinator() = preferencesHelper.getUserId() in args.coordinators


    private fun onMenuSetStartClick(attraction: Attraction) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.updateStartingPointAsync(attraction.id).await()) {
                is Resource.Success -> {
                    viewModel.refresh()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuSetStartClick(attraction)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }

    private fun onMenuEditClick(attraction: Attraction) {
        findNavController().navigate(
            AttractionsFragmentDirections.actionAttractionsFragmentToCreateEditAttractionFragment(
                args.dayPlan.id,
                attraction
            )
        )
    }

    private fun onMenuDeleteClick(attraction: Attraction) {
        val deleteDialog = DeleteAttractionDialog(this, attraction)
        deleteDialog.show(childFragmentManager, DeleteAttractionDialog.TAG)
    }

    override fun onAddMoreClick() {
        findNavController().navigate(
            AttractionsFragmentDirections.actionAttractionsFragmentToFindAttractionFragment(
                args.groupId,
                args.dayPlan.id
            )
        )
    }

    //dialogs
    override fun onDeleteClick(attraction: Attraction) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.deleteAttractionAsync(attraction.id).await()) {
                is Resource.Success -> {
                    viewModel.refresh()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuDeleteClick(attraction)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }
}