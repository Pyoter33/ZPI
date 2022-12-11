package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.BalancesAdapter
import com.example.trip.databinding.FragmentBalancesBinding
import com.example.trip.models.Resource
import com.example.trip.viewmodels.finances.FinancesViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BalancesFragment @Inject constructor() : BaseFragment<FragmentBalancesBinding>() {

    @Inject
    lateinit var adapter: BalancesAdapter

    private val viewModel: FinancesViewModel by hiltNavGraphViewModels(R.id.finances)

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBalancesBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        observeLists()
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        binding.listBalances.adapter = adapter
        binding.listBalances.layoutManager = layoutManager
        adapter.setCurrency(requireArguments().getString(Constants.CURRENCY_KEY, "?"))
        binding.listBalances.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )
    }

    private fun observeLists() {
        viewModel.balances.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                }
                is Resource.Loading -> {
                    //NO-OP
                }
                is Resource.Failure -> {
                    it.message?.let {
                        (requireActivity() as MainActivity).showSnackbar(
                            requireView(),
                            it,
                            R.string.text_retry
                        ) { viewModel.refreshDataExpense() }
                    } ?: (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) { viewModel.refreshDataBalances() }
                }
            }
        }
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}