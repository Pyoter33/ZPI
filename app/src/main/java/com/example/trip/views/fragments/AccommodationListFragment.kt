package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.AccommodationClickListener
import com.example.trip.adapters.AccommodationListAdapter
import com.example.trip.databinding.FragmentAccommodationListBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.accommodation.AccommodationsListViewModel
import com.example.trip.views.dialogs.MenuPopupAcceptFactory
import com.example.trip.views.dialogs.accommodation.AcceptAccommodationDialog
import com.example.trip.views.dialogs.accommodation.AcceptAccommodationDialogClickListener
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialog
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccommodationListFragment @Inject constructor() : Fragment(), AccommodationClickListener,
    AcceptAccommodationDialogClickListener, DeleteAccommodationDialogClickListener {

    private lateinit var binding: FragmentAccommodationListBinding
    private val popupMenu by balloon<MenuPopupAcceptFactory>()

    @Inject
    lateinit var adapter: AccommodationListAdapter

    private val viewModel: AccommodationsListViewModel by viewModels()

    private val args: AccommodationListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccommodationListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        popupMenu.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        requireActivity().onBackArrowClick(binding.buttonBack)
        observeAccommodationsList()
        setOnCheckedChipsListener()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        onAddClick()
    }


    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                AccommodationListFragmentDirections.actionAccommodationListFragmentToCreateEditAccommodationFragment(
                    args.groupId,
                    args.currency
                )
            )
        }
    }

    private fun observeAccommodationsList() {
        viewModel.accommodationsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if(it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
                    adapter.submitList(it.data)
                    binding.chipGroup.clearCheck()
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
        adapter.setAccommodationClickListener(this)
        adapter.setPopupMenu(popupMenu)
        adapter.setCurrency(args.currency)
        binding.accommodationList.adapter = adapter
        binding.accommodationList.layoutManager = LinearLayoutManager(context)
    }

    private fun setOnCheckedChipsListener() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            when (checkedIds.size) {
                0 -> {
                    applyFilter(viewModel.resetFilter())
                }
                1 -> {
                    when (checkedIds.first()) {
                        R.id.chip_votes -> {
                            applyFilter(viewModel.filterVoted())
                        }
                        R.id.chip_accommodations -> {
                            applyFilter(viewModel.filterAccommodations(PLACEHOLDER_USERID))
                        }
                    }
                }
                2 -> {
                    applyFilter(viewModel.filterVotedAccommodations(PLACEHOLDER_USERID))
                }
            }
        }
    }

    private fun applyFilter(result: Resource<List<Accommodation>>) {
        result.let {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
                else -> {}
            }
        }
    }

    //list item
    override fun onVoteClick(position: Int, button: View) {
        viewModel.setVoted(position)
        adapter.notifyItemChanged(position)
        button.isEnabled = false
        lifecycleScope.launch {
            when (viewModel.waitForDelay()) { //wait for response from backend
                is Resource.Success -> {
                    button.isEnabled = true
                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_post_failure)
                }
                else -> {}
            }
        }
    }

    override fun onExpandClick(position: Int) {
        viewModel.setExpanded(position)
        adapter.notifyItemChanged(position)
    }

    override fun onLinkClick(accommodation: Accommodation) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(accommodation.sourceUrl)
        )
        startActivity(intent)
    }

    override fun onTransportClick(accommodation: Accommodation) {
        val bundle = Bundle().apply { putLong(Constants.GROUP_ID_KEY, accommodation.groupId)
            putLong(Constants.ACCOMMODATION_ID_KEY, accommodation.id)
            putString(Constants.DESTINATION_KEY, accommodation.address)
            putString(Constants.START_CITY_KEY, args.startCity)
            putString(Constants.CURRENCY_KEY, args.currency)}

        findNavController().navigate(R.id.transport, bundle)
    }

    override fun onMenuAcceptClick(accommodation: Accommodation) {
        val acceptDialog = AcceptAccommodationDialog(this, accommodation)
        acceptDialog.show(childFragmentManager, AcceptAccommodationDialog.TAG)
    }

    override fun onMenuEditClick(accommodation: Accommodation) {
        findNavController().navigate(
            AccommodationListFragmentDirections.actionAccommodationListFragmentToCreateEditAccommodationFragment(
                args.groupId,
                args.currency,
                accommodation.id,
                accommodation
            )
        )
    }

    override fun onMenuDeleteClick(accommodation: Accommodation) {
        val deleteDialog = DeleteAccommodationDialog(this, accommodation)
        deleteDialog.show(childFragmentManager, DeleteAccommodationDialog.TAG)
    }

    //dialogs
    override fun onAcceptClick(accommodation: Accommodation) {
        requireContext().toast("accept")
    }

    override fun onDeleteClick(accommodation: Accommodation) {
        requireContext().toast("delete")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
        private const val START_CITY_ARG = "startCity"
    }

}