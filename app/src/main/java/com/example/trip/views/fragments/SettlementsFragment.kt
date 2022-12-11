package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.SettlementClickListener
import com.example.trip.adapters.SettlementHeaderAdapter
import com.example.trip.adapters.SettlementOtherAdapter
import com.example.trip.adapters.SettlementUserAdapter
import com.example.trip.databinding.FragmentSettlementsBinding
import com.example.trip.models.Resource
import com.example.trip.models.Settlement
import com.example.trip.models.SettlementStatus
import com.example.trip.utils.*
import com.example.trip.viewmodels.finances.FinancesViewModel
import com.example.trip.views.dialogs.MenuPopupResolveFactory
import com.example.trip.views.dialogs.finances.ResolveSettlementDialog
import com.example.trip.views.dialogs.finances.ResolveSettlementDialogClickListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettlementsFragment @Inject constructor() : BaseFragment<FragmentSettlementsBinding>(),
    SettlementClickListener, ResolveSettlementDialogClickListener {

    private val popupMenu by balloon<MenuPopupResolveFactory>()

    @Inject
    lateinit var userAdapter: SettlementUserAdapter

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var otherAdapter: SettlementOtherAdapter

    private val viewModel: FinancesViewModel by hiltNavGraphViewModels(R.id.finances)

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettlementsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(
            binding.layoutRefresh,
            R.color.secondary
        ) { viewModel.refreshDataSettlements() }
        setAdapters()
        observeSettlementsList()
        onFinancesClick()
    }

    private fun onFinancesClick() {
        binding.buttonSwitchToFinances.setOnClickListener {
            (requireParentFragment() as MoneyPager).switchToFinancesFragment()
        }
    }

    private fun observeSettlementsList() {
        val userId = preferencesHelper.getUserId()
        viewModel.settlementsList.observe(viewLifecycleOwner) { settlement ->
            when (settlement) {
                is Resource.Success -> {
                    if (settlement.data.isEmpty()) {
                        binding.textEmptyList.setVisible()
                        binding.listSettlements.setInvisible()
                    } else {
                        binding.textEmptyList.setGone()
                        binding.listSettlements.setVisible()
                    }
                    binding.layoutRefresh.isRefreshing = false
                    val userSettlements = settlement.data.filter { it.debtee.id == userId || it.debtor.id == userId }
                    val otherSettlements = settlement.data.minus(userSettlements.toSet())
                    userAdapter.submitList(userSettlements)
                    otherAdapter.submitList(otherSettlements)
                }
                is Resource.Loading -> {
                    binding.textEmptyList.setGone()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    settlement.message?.let {
                        (requireActivity() as MainActivity).showSnackbar(
                            requireView(),
                            it,
                            R.string.text_retry
                        ) {
                            viewModel.refreshDataSettlements()
                        }
                    } ?: (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refreshDataSettlements()
                    }
                    binding.textEmptyList.setGone()
                }
            }
        }
    }

    private fun setAdapters() {
        val currency = requireArguments().getString(Constants.CURRENCY_KEY, "?")
        val layoutManager = LinearLayoutManager(context)
        userAdapter.setSettlementClickListener(this)
        userAdapter.setCurrency(currency)
        otherAdapter.setCurrency(currency)
        val headerAdapterUser = SettlementHeaderAdapter()
        headerAdapterUser.submitList(listOf(getString(R.string.text_my_settlements)))
        val headerAdapterOther = SettlementHeaderAdapter()
        headerAdapterOther.submitList(listOf(getString(R.string.text_other_settlements)))

        val concatAdapter = ConcatAdapter(headerAdapterUser, userAdapter, headerAdapterOther, otherAdapter)
        binding.listSettlements.adapter = concatAdapter
        binding.listSettlements.layoutManager = layoutManager
        binding.listSettlements.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )
    }

    //list item
    private fun onMenuResolve(settlement: Settlement) {
        val resolveDialog = ResolveSettlementDialog(this, settlement)
        resolveDialog.show(childFragmentManager, ResolveSettlementDialog.TAG)
    }

    override fun onLongClick(settlement: Settlement, view: View) {
        if (settlement.status != SettlementStatus.RESOLVED) {
            popupMenu.setOnPopupButtonClick(R.id.button_resolve) {
                onMenuResolve(settlement)
            }
            popupMenu.showAlignBottom(view)
        }
    }

    //dialogs
    override fun onResolveClick(settlement: Settlement) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.resolveSettlementAsync(settlement.id).await()) {
                is Resource.Success -> {
                    viewModel.refreshDataSettlements()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuResolve(settlement)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }

}