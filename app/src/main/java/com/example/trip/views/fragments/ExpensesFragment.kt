package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ExpenseClickListener
import com.example.trip.adapters.ExpensesAdapter
import com.example.trip.databinding.FragmentExpensesBinding
import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.utils.getLongFromBundle
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.setSwipeRefreshLayout
import com.example.trip.utils.toast
import com.example.trip.viewmodels.finances.ExpensesViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialog
import com.example.trip.views.dialogs.finances.DeleteExpenseDialog
import com.example.trip.views.dialogs.finances.DeleteExpenseDialogClickListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ExpensesFragment @Inject constructor() : Fragment(), ExpenseClickListener, DeleteExpenseDialogClickListener{

    private lateinit var binding: FragmentExpensesBinding
    private val popupMenu by balloon<MenuPopupFactory>()

    private var groupId by Delegates.notNull<Long>()

    @Inject
    lateinit var adapter: ExpensesAdapter

    private val viewModel: ExpensesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpensesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = getLongFromBundle(GROUP_ID_ARG)
        setAdapter()
        requireActivity().onBackArrowClick(binding.buttonBack)
        observeAccommodationsList()
        setOnCheckedChipsListener()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        onAddClick()
    }

    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {

        }
    }

    private fun observeAccommodationsList() {
        viewModel.expensesList.observe(viewLifecycleOwner) {
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
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
                    ) {
                        viewModel.refreshData()
                    }
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setExpenseClickListener(this)
        adapter.setPopupMenu(popupMenu)
        binding.listExpenses.adapter = adapter
        binding.listExpenses.layoutManager = layoutManager
        binding.listExpenses.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            ).apply {
                isLastItemDecorated = false
            }
        )
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
                            applyFilter(viewModel.filterMyExpenses(PLACEHOLDER_USERID))
                        }
                        R.id.chip_accommodations -> {
                            applyFilter(viewModel.filterContributions(PLACEHOLDER_USERID))
                        }
                    }
                }
                2 -> {
                    applyFilter(viewModel.filterMyExpensesContributions(PLACEHOLDER_USERID))
                }
            }
        }
    }

    private fun applyFilter(result: Resource<List<Expense>>) {
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

    override fun onMenuEditClick(expense: Expense) {

    }

    override fun onMenuDeleteClick(expense: Expense) {
        val deleteDialog = DeleteExpenseDialog(this, expense)
        deleteDialog.show(childFragmentManager, DeleteAccommodationDialog.TAG)
    }

    //dialogs
    override fun onDeleteClick(expense: Expense) {
        requireContext().toast("delete")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}