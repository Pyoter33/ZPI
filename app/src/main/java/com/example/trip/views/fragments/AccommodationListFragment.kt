package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
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
import com.example.trip.models.UserType
import com.example.trip.utils.*
import com.example.trip.viewmodels.accommodation.AccommodationsListViewModel
import com.example.trip.views.dialogs.MenuPopupAcceptFactory
import com.example.trip.views.dialogs.MenuPopupFactory
import com.example.trip.views.dialogs.TransportDialog
import com.example.trip.views.dialogs.accommodation.AcceptAccommodationDialog
import com.example.trip.views.dialogs.accommodation.AcceptAccommodationDialogClickListener
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialog
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AccommodationListFragment @Inject constructor() :
    BaseFragment<FragmentAccommodationListBinding>(), AccommodationClickListener,
    AcceptAccommodationDialogClickListener, DeleteAccommodationDialogClickListener {

    private val popupMenuCreator by balloon<MenuPopupFactory>()

    private val popupMenuCoordinator by balloon<MenuPopupAcceptFactory>()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var adapter: AccommodationListAdapter

    private val viewModel: AccommodationsListViewModel by viewModels()

    private val args: AccommodationListFragmentArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccommodationListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        requireActivity().onBackArrowClick(binding.buttonBack)
        observeAccommodationsList()
        setOnCheckedChipsListener()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        refreshIfNewData { viewModel.refreshData() }
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
                    if (it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
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
        adapter.setCurrency(args.currency)
        binding.accommodationList.adapter = adapter
        binding.accommodationList.layoutManager = LinearLayoutManager(context)
    }

    private fun setOnCheckedChipsListener() {
        val userId = preferencesHelper.getUserId()
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
                            applyFilter(viewModel.filterAccommodations(userId))
                        }
                    }
                }
                2 -> {
                    applyFilter(viewModel.filterVotedAccommodations(userId))
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
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refreshData()
                    }
                }
                else -> {}
            }
        }
    }

    //list item
    override fun onVoteClick(accommodation: Accommodation, position: Int, button: View) {
        val operation =
            if (accommodation.isVoted) viewModel.deleteVoteAsync(accommodation.id) else viewModel.postVoteAsync(
                accommodation.id
            )
        viewModel.setVoted(position)
        adapter.notifyItemChanged(position)
        button.isEnabled = false
        lifecycleScope.launch {
            when (operation.await()) {
                is Resource.Success -> {
                    button.isEnabled = true
                }
                is Resource.Failure -> {
                    viewModel.setVoted(position)
                    adapter.notifyItemChanged(position)
                    button.isEnabled = true
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        onVoteClick(accommodation, position, button)
                    }
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
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (val result = viewModel.getAcceptedAvailabilityAsync().await()) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                    result.data?.let {
                        navigateToTransport(accommodation, it.availability.startDate)
                    } ?: showTransportDialog()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        onTransportClick(accommodation)
                    }
                }
                else -> {}
            }
        }
    }

    private fun navigateToTransport(accommodation: Accommodation, startDate: LocalDate) {
        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()
        val bundle = Bundle().apply {
            putLong(Constants.GROUP_ID_KEY, accommodation.groupId)
            putLong(Constants.ACCOMMODATION_ID_KEY, accommodation.id)
            putString(Constants.DESTINATION_KEY, accommodation.address)
            putString(Constants.START_CITY_KEY, args.startCity)
            putString(Constants.CURRENCY_KEY, args.currency)
            putLong(Constants.START_DATE_KEY, startDate.toMillis())
            putLong(Constants.ACCOMMODATION_CREATOR_ID_KEY, accommodation.creatorId)
            putLongArray(Constants.COORDINATORS_KEY, args.coordinators)
        }
        findNavController().navigate(R.id.transport, bundle, options)
    }

    private fun showTransportDialog() {
        val transportDialog = TransportDialog()
        transportDialog.show(childFragmentManager, TransportDialog.TAG)
    }

    override fun onLongClick(accommodation: Accommodation, view: View) {
        val popupMenu = when (getUserType(accommodation.creatorId)) {
            UserType.COORDINATOR -> {
                if(accommodation.isAccepted) {
                    requireContext().toast(R.string.text_cannot_modify_accommodation)
                    null
                } else {
                    popupMenuCoordinator.apply {
                        setOnPopupButtonClick(R.id.button_accept) { onMenuAcceptClick(accommodation) }
                        setOnPopupButtonClick(R.id.button_edit) { onMenuEditClick(accommodation) }
                        setOnPopupButtonClick(R.id.button_delete) { onMenuDeleteClick(accommodation) }
                    }
                }
            }
            UserType.CREATOR -> {
                if(accommodation.isAccepted) {
                    requireContext().toast(R.string.text_cannot_modify_accommodation)
                    null
                } else {
                    popupMenuCreator.apply {
                        setOnPopupButtonClick(R.id.button_edit) { onMenuEditClick(accommodation) }
                        setOnPopupButtonClick(R.id.button_delete) { onMenuDeleteClick(accommodation) }
                    }
                }
            }
            else -> null
        }
        popupMenu?.showAlignBottom(view)
    }

    private fun getUserType(creatorId: Long): UserType {
        return when (preferencesHelper.getUserId()) {
            in args.coordinators -> UserType.COORDINATOR
            creatorId -> UserType.CREATOR
            else -> UserType.BASIC_USER
        }
    }

    private fun onMenuAcceptClick(accommodation: Accommodation) {
        val acceptDialog = AcceptAccommodationDialog(this, accommodation)
        acceptDialog.show(childFragmentManager, AcceptAccommodationDialog.TAG)
    }

    private fun onMenuEditClick(accommodation: Accommodation) {
        findNavController().navigate(
            AccommodationListFragmentDirections.actionAccommodationListFragmentToCreateEditAccommodationFragment(
                args.groupId,
                args.currency,
                accommodation.id,
                accommodation
            )
        )
    }

    private fun onMenuDeleteClick(accommodation: Accommodation) {
        val deleteDialog = DeleteAccommodationDialog(this, accommodation)
        deleteDialog.show(childFragmentManager, DeleteAccommodationDialog.TAG)
    }

    //dialogs
    override fun onAcceptClick(accommodation: Accommodation) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.postAcceptedAccommodationAsync(accommodation.id).await()) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                    requireContext().toast(R.string.text_accommodation_accepted)
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuDeleteClick(accommodation)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }

    override fun onDeleteClick(accommodation: Accommodation) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.deleteAccommodationAsync(accommodation.id).await()) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                    viewModel.refreshData()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuDeleteClick(accommodation)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }
}