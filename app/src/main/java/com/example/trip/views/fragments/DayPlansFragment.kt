package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.DayPlansAdapter
import com.example.trip.adapters.DayPlansClickListener
import com.example.trip.databinding.FragmentDayPlansBinding
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.dayplan.DayPlansViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.example.trip.views.dialogs.dayplan.DeleteDayPlanDialog
import com.example.trip.views.dialogs.dayplan.DeleteDayPlanDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DayPlansFragment @Inject constructor() : BaseFragment<FragmentDayPlansBinding>(),
    DayPlansClickListener,
    DeleteDayPlanDialogClickListener {

    private val popupMenu by balloon<MenuPopupFactory>()

    @Inject
    lateinit var adapter: DayPlansAdapter

    private val viewModel: DayPlansViewModel by viewModels()

    private val args: DayPlansFragmentArgs by navArgs()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDayPlansBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackArrowClick(binding.buttonBack)
        setAdapter()
        observeDayPlansList()
        setSwipeRefreshLayout()
        onAddClick()
        refreshIfNewData { viewModel.refresh() }
    }

    private fun setSwipeRefreshLayout() {
        binding.layoutRefresh.setColorSchemeResources(R.color.primary)
        binding.layoutRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun onAddClick() {
        if (!isCoordinator()) {
            binding.buttonAdd.setGone()
            return
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                DayPlansFragmentDirections.actionDayPlansFragmentToCreateEditDayPlanFragment(
                    args.groupId
                )
            )
        }
    }

    private fun observeDayPlansList() {
        viewModel.dayPlans.observe(viewLifecycleOwner) {
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
                    it.message?.let {
                        (requireActivity() as MainActivity).showSnackbar(
                            requireView(),
                            it,
                            R.string.text_retry
                        ) {
                            viewModel.refresh()
                        }
                    } ?: (requireActivity() as MainActivity).showSnackbar(
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
        adapter.setDayPlansClickListener(this)
        binding.attractionsList.adapter = adapter
    }

    private fun isCoordinator() = preferencesHelper.getUserId() in args.coordinators

    //list item
    override fun onClick(dayPlan: DayPlan) {
        findNavController().navigate(
            DayPlansFragmentDirections.actionDayPlansFragmentToAttractionsFragment(
                args.groupId,
                dayPlan,
                args.coordinators
            )
        )
    }

    override fun onLongClick(dayPlan: DayPlan, view: View) {
        if (isCoordinator()) {
            popupMenu.setOnPopupButtonClick(R.id.button_edit) {
                onMenuEditClick(dayPlan)
            }
            popupMenu.setOnPopupButtonClick(R.id.button_delete) {
                onMenuDeleteClick(dayPlan)
            }
            popupMenu.showAlignBottom(view)
        }
    }

    private fun onMenuEditClick(dayPlan: DayPlan) {
        findNavController().navigate(
            DayPlansFragmentDirections.actionDayPlansFragmentToCreateEditDayPlanFragment(
                args.groupId, dayPlan
            )
        )
    }

    private fun onMenuDeleteClick(dayPlan: DayPlan) {
        val deleteDialog = DeleteDayPlanDialog(this, dayPlan)
        deleteDialog.show(childFragmentManager, DeleteDayPlanDialog.TAG)
    }

    //dialogs
    override fun onDeleteClick(dayPlan: DayPlan) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.deleteDayPlanAsync(dayPlan.id).await()) {
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
                        onMenuDeleteClick(dayPlan)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }
}