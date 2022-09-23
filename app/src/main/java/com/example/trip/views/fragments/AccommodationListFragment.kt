package com.example.trip.views.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.adapters.AccommodationClickListener
import com.example.trip.adapters.AccommodationListAdapter
import com.example.trip.databinding.FragmentAccommodationListBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.utils.toast
import com.example.trip.viewmodels.accommodation.AccommodationsListViewModel
import com.example.trip.views.dialogs.AcceptAccommodationDialog
import com.example.trip.views.dialogs.AcceptAccommodationDialogClickListener
import com.example.trip.views.dialogs.DeleteAccommodationDialog
import com.example.trip.views.dialogs.DeleteAccommodationDialogClickListener
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AccommodationListFragment @Inject constructor() : Fragment(), AccommodationClickListener,
    AcceptAccommodationDialogClickListener, DeleteAccommodationDialogClickListener {

    private lateinit var binding: FragmentAccommodationListBinding

    @Inject
    lateinit var adapter: AccommodationListAdapter

    private val viewModel: AccommodationsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccommodationListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        observeAccommodationsList()
        setOnCheckedChipsListener()
        setSwipeRefreshLayout()
        onAddClick()
    }

    private fun setSwipeRefreshLayout() {
        binding.layoutRefresh.setColorSchemeResources(R.color.primary)
        binding.layoutRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(AccommodationListFragmentDirections.actionAccommodationListFragmentToCreateEditAccommodationFragment())
        }
    }

    private fun observeAccommodationsList() {
        viewModel.accommodationsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.chipGroup.clearCheck()
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
        adapter.setAccommodationClickListener(this)
        //binding.accommodationList.itemAnimator = null
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

    private fun createPopupMenu(view: View, accommodation: Accommodation) {
        val itemsCoordinator = listOf(
            PowerMenuItem(TEXT_ACCEPT, R.drawable.ic_baseline_check_24).apply {
                tag = R.id.accept;
            },
            PowerMenuItem(TEXT_EDIT, R.drawable.ic_baseline_edit_24).apply { tag = R.id.edit },
            PowerMenuItem(TEXT_DELETE, R.drawable.ic_baseline_delete_24).apply { tag = R.id.delete }
        )

        val itemsCreator = listOf(
            PowerMenuItem(TEXT_EDIT, R.drawable.ic_baseline_edit_24).apply { tag = R.id.edit },
            PowerMenuItem(TEXT_DELETE, R.drawable.ic_baseline_delete_24).apply { tag = R.id.delete }
        )

        val powerMenu = PowerMenu.Builder(requireContext())
            .addItemList(itemsCoordinator)
            .setTextColor(resources.getColor(R.color.grey700, null))
            .setIconColor(resources.getColor(R.color.primary, null))
            .setAnimation(MenuAnimation.FADE)
            .setMenuRadius(10f)
            .setAutoDismiss(true)
            .setMenuColor(Color.WHITE)
            .setLifecycleOwner(viewLifecycleOwner)
            .setOnMenuItemClickListener { _, item ->
                when (item.tag) {
                    R.id.accept -> showAcceptDialog(accommodation)
                    R.id.edit -> onEditClick(accommodation)
                    else -> showDeleteDialog(accommodation)
                }
            }
            .build()
        powerMenu.showAsAnchorRightTop(view)
    }

    private fun showAcceptDialog(accommodation: Accommodation) {
        val acceptDialog = AcceptAccommodationDialog(this, accommodation)
        acceptDialog.show(childFragmentManager, AcceptAccommodationDialog.TAG)
    }

    private fun onEditClick(accommodation: Accommodation) {
        findNavController().navigate(
            AccommodationListFragmentDirections.actionAccommodationListFragmentToCreateEditAccommodationFragment(
                accommodation.id,
                accommodation.sourceUrl,
                accommodation.description
            )
        )
    }

    private fun showDeleteDialog(accommodation: Accommodation) {
        val deleteDialog = DeleteAccommodationDialog(this, accommodation)
        deleteDialog.show(childFragmentManager, DeleteAccommodationDialog.TAG)
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

    override fun onLinkClick() {

    }

    override fun onTransportClick() {

    }

    override fun onLongClick(accommodation: Accommodation, view: View) {
        requireActivity()
        createPopupMenu(view, accommodation)
    }

    //dialogs
    override fun onAcceptClick(accommodation: Accommodation) {
        requireContext().toast("accept")
    }

    override fun onDeleteClick(accommodation: Accommodation) {
        requireContext().toast("delete")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
        private const val TEXT_ACCEPT = "Accept"
        private const val TEXT_EDIT = "Edit"
        private const val TEXT_DELETE = "Delete"
    }

}