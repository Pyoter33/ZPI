package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ExpenseClickListener
import com.example.trip.adapters.ExpensesAdapter
import com.example.trip.databinding.FragmentExpensesBinding
import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.utils.toast
import com.example.trip.viewmodels.finances.FinancesViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExpensesFragment @Inject constructor() : Fragment(), ExpenseClickListener {

    private lateinit var binding: FragmentExpensesBinding

    @Inject
    lateinit var adapter: ExpensesAdapter

    private val viewModel: FinancesViewModel by hiltNavGraphViewModels(R.id.finances)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpensesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        observeExpensesList()
        setOnCheckedChipsListener()
    }

    private fun observeExpensesList() {
        viewModel.expensesList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.chipGroup.clearCheck()
                }
                is Resource.Loading -> {
                    //NO-OP
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
                    ) {
                        viewModel.refreshDataExpense()
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setExpenseClickListener(this)
        adapter.setCurrency(requireArguments().getString(Constants.CURRENCY_KEY, "?"))
        binding.listExpenses.adapter = adapter
        binding.listExpenses.layoutManager = layoutManager
        binding.listExpenses.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
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
                        R.id.chip_expenses -> {
                            applyFilter(viewModel.filterMyExpenses(PLACEHOLDER_USERID))
                        }
                        R.id.chip_contributions -> {
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
                    requireContext().toast(R.string.text_fetch_failure) //snackbar
                }
                else -> {}
            }
        }
    }

    //list item
    override fun onClick(expense: Expense) {
        (requireParentFragment() as FinancesFragment).onExpenseClick(expense)
    }


    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}