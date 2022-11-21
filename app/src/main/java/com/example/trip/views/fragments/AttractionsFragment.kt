package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toast
import com.example.trip.viewmodels.dayplan.AttractionsViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.example.trip.views.dialogs.dayplan.DeleteAttractionDialog
import com.example.trip.views.dialogs.dayplan.DeleteAttractionDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AttractionsFragment @Inject constructor() : BaseFragment<FragmentAttractionsBinding>(), AttractionClickListener,
    DeleteAttractionDialogClickListener {

    private val popupMenu by balloon<MenuPopupFactory>()

    private val args: AttractionsFragmentArgs by navArgs()

    @Inject
    lateinit var adapter: AttractionListAdapter

    private val viewModel: AttractionsViewModel by viewModels()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAttractionsBinding.inflate(inflater, container, false)

    override fun onStop() {
        super.onStop()
        popupMenu.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setupArgs()
        observeAccommodationsList()
        setSwipeRefreshLayout()
        onAddClick()
        onBackArrowClick(binding.buttonBack)
    }

    private fun setupArgs() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        binding.textHeader.text = args.dayPlan.name
        binding.textHeaderDate.text = args.dayPlan.date.format(formatter)
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
                    if(it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
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
                        viewModel.refreshData()
                    }
                    binding.layoutRefresh.isRefreshing = false
                    binding.textEmptyList.setGone()
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

    private fun onAddClick() {
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

    override fun onMenuEditClick(attraction: Attraction) {
        findNavController().navigate(
            AttractionsFragmentDirections.actionAttractionsFragmentToCreateEditAttractionFragment(
                attraction
            )
        )
    }

    override fun onMenuDeleteClick(attraction: Attraction) {
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
        requireContext().toast("delete")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
    }
}