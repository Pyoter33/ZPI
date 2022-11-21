package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.trip.R
import com.example.trip.adapters.FinancesPagerAdapter
import com.example.trip.databinding.FragmentFinancesBinding
import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.setSwipeRefreshLayout
import com.example.trip.viewmodels.finances.FinancesViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FinancesFragment @Inject constructor() : BaseFragment<FragmentFinancesBinding>() {

    private lateinit var adapter: FinancesPagerAdapter

    private val viewModel: FinancesViewModel by hiltNavGraphViewModels(R.id.finances)

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFinancesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPager()
        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) {
            viewModel.refreshDataExpense()
            viewModel.refreshDataBalances()
        }
        observeLists()
        onBalancesClick()
        onAddClick()
    }

    fun onExpenseClick(expense: Expense) {
        (requireParentFragment() as MoneyPager).navigateToExpenseDetailsFragment(expense)
    }


    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {
            (requireParentFragment() as MoneyPager).navigateToCreateEditFragment()
        }
    }

    private fun onBalancesClick() {
        binding.buttonSwitchToBalance.setOnClickListener {
            (requireParentFragment() as MoneyPager).switchToSettlementsFragment()
        }
    }

    private fun observeLists() {
        viewModel.expensesList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    //NO-OP
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }

        viewModel.balances.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    //NO-OP
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setPager() {
        adapter = FinancesPagerAdapter(
            listOf(
                ExpensesFragment(),
                BalancesFragment()
            ),
            childFragmentManager,
            lifecycle,
            requireArguments()
        )
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabPager, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.text_expenses)
                    tab.setIcon(R.drawable.ic_baseline_receipt_24)
                }
                1 -> {
                    tab.text = getString(R.string.text_balances)
                    tab.setIcon(R.drawable.ic_baseline_sync_alt_24)
                }
            }
        }.attach()
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}