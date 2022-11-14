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
import com.example.trip.adapters.BalancesAdapter
import com.example.trip.databinding.FragmentBalancesBinding
import com.example.trip.models.Resource
import com.example.trip.utils.getLongFromBundle
import com.example.trip.viewmodels.finances.FinancesViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class BalancesFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentBalancesBinding
    private var groupId by Delegates.notNull<Long>()

    @Inject
    lateinit var adapter: BalancesAdapter

    private val viewModel: FinancesViewModel by hiltNavGraphViewModels(R.id.finances)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBalancesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = getLongFromBundle(GROUP_ID_ARG)
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

                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
                    ) {
                        viewModel.refreshDataBalances()
                    }
                }
            }
        }
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}