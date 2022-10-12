package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.adapters.AttractionClickListener
import com.example.trip.adapters.AttractionListAdapter
import com.example.trip.databinding.FragmentAttractionsBinding
import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.utils.toast
import com.example.trip.viewmodels.dayplan.AttractionsViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.example.trip.views.dialogs.attraction.DeleteAttractionDialog
import com.example.trip.views.dialogs.attraction.DeleteAttractionDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AttractionsFragment @Inject constructor() : Fragment(), AttractionClickListener,
    DeleteAttractionDialogClickListener {

    private lateinit var binding: FragmentAttractionsBinding
    private val popupMenu by balloon<MenuPopupFactory>()

    private val args: AttractionsFragmentArgs by navArgs()

    @Inject
    lateinit var adapter: AttractionListAdapter

    private val viewModel: AttractionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttractionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        popupMenu.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        observeAccommodationsList()
        setSwipeRefreshLayout()
    }

    private fun setSwipeRefreshLayout() {
        binding.layoutRefresh.setColorSchemeResources(R.color.primary)
        binding.layoutRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun observeAccommodationsList() {
        viewModel.attractionsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setAdapter() {
        adapter.setAttractionClickListener(this)
        adapter.setPopupMenu(popupMenu)
        binding.attractionsList.adapter = adapter
        binding.attractionsList.layoutManager = LinearLayoutManager(context)
    }

    //list item
    override fun onExpandClick(position: Int) {
        viewModel.setExpanded(position)
        adapter.notifyItemChanged(position)
    }

    override fun onSeeMoreClick(link: String) {

    }

    override fun onMenuEditClick(attraction: Attraction) {

    }

    override fun onMenuDeleteClick(attraction: Attraction) {
        val deleteDialog = DeleteAttractionDialog(this, attraction)
        deleteDialog.show(childFragmentManager, DeleteAttractionDialog.TAG)
    }

    override fun onAddMoreClick() {
        findNavController().navigate(
            AttractionsFragmentDirections.actionAttractionsFragmentToFindAttractionFragment(
                args.groupId,
                args.dayPlanId
            )
        )
    }

    //dialogs
    override fun onDeleteClick(attraction: Attraction) {
        requireContext().toast("delete")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
    }
}