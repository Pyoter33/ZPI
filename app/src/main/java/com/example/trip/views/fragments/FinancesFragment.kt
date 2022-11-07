package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trip.R
import com.example.trip.adapters.ExpensesAdapter
import com.example.trip.adapters.FinancesPagerAdapter
import com.example.trip.databinding.FragmentFinancesBinding
import com.example.trip.models.Resource
import com.example.trip.utils.getLongFromBundle
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.setSwipeRefreshLayout
import com.example.trip.viewmodels.finances.ExpensesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates


@AndroidEntryPoint
class FinancesFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentFinancesBinding
    private var groupId by Delegates.notNull<Long>()

    @Inject
    lateinit var adapter: ExpensesAdapter

    private val viewModel: ExpensesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinancesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = getLongFromBundle(GROUP_ID_ARG)
        setPager()
        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        onAddClick()
    }

    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {

        }
    }

    private fun observeExpensesList() {
        viewModel.expensesList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }
    }


    private fun setPager() {
        val adapter = FinancesPagerAdapter()
        binding.viewPager.adapter
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}