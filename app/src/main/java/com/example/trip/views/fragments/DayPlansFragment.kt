package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.DayPlansAdapter
import com.example.trip.adapters.DayPlansClickListener
import com.example.trip.databinding.FragmentDayPlansBinding
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.toast
import com.example.trip.viewmodels.dayplan.DayPlansViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.example.trip.views.dialogs.dayplan.DeleteDayPlanDialog
import com.example.trip.views.dialogs.dayplan.DeleteDayPlanDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DayPlansFragment @Inject constructor() : Fragment(), DayPlansClickListener,
    DeleteDayPlanDialogClickListener {

    private lateinit var binding: FragmentDayPlansBinding
    private val popupMenu by balloon<MenuPopupFactory>()

    @Inject
    lateinit var adapter: DayPlansAdapter

    private val viewModel: DayPlansViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayPlansBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        popupMenu.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackArrowClick(binding.buttonBack)
        setAdapter()
        observeDayPlansList()
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
            findNavController().navigate(
                DayPlansFragmentDirections.actionDayPlansFragmentToCreateEditDayPlanFragment(
                    0
                )
            )
        }
    }

    private fun observeDayPlansList() {
        viewModel.dayPlans.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
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
                }
            }
        }
    }

    private fun setAdapter() {
        adapter.setDayPlansClickListener(this)
        adapter.setPopupMenu(popupMenu)
        binding.attractionsList.adapter = adapter
    }

    //list item
    override fun onClick(dayPlan: DayPlan) {
        findNavController().navigate(
            DayPlansFragmentDirections.actionDayPlansFragmentToAttractionsFragment(
                0,
                dayPlan
            )
        )
    }

    override fun onMenuEditClick(dayPlan: DayPlan) {
        findNavController().navigate(
            DayPlansFragmentDirections.actionDayPlansFragmentToCreateEditDayPlanFragment(
                0, dayPlan
            )
        )
    }

    override fun onMenuDeleteClick(dayPlan: DayPlan) {
        val deleteDialog = DeleteDayPlanDialog(this, dayPlan)
        deleteDialog.show(childFragmentManager, DeleteDayPlanDialog.TAG)
    }


    //dialogs
    override fun onDeleteClick(dayPlan: DayPlan) {
        requireContext().toast("delete")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
    }
}