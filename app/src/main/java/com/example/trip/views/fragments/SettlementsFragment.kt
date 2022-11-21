package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.SettlementClickListener
import com.example.trip.adapters.SettlementOtherAdapter
import com.example.trip.adapters.SettlementUserAdapter
import com.example.trip.databinding.FragmentSettlementsBinding
import com.example.trip.models.Resource
import com.example.trip.models.Settlement
import com.example.trip.utils.*
import com.example.trip.viewmodels.finances.FinancesViewModel
import com.example.trip.views.dialogs.MenuPopupResolveFactory
import com.example.trip.views.dialogs.finances.ResolveSettlementDialog
import com.example.trip.views.dialogs.finances.ResolveSettlementDialogClickListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class SettlementsFragment @Inject constructor() : BaseFragment<FragmentSettlementsBinding>(), SettlementClickListener, ResolveSettlementDialogClickListener{

    private val popupMenu by balloon<MenuPopupResolveFactory>()

    private var groupId by Delegates.notNull<Long>()

    @Inject
    lateinit var userAdapter: SettlementUserAdapter

    @Inject
    lateinit var otherAdapter: SettlementOtherAdapter

    private val viewModel: FinancesViewModel by hiltNavGraphViewModels(R.id.finances)

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettlementsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = getLongFromBundle(GROUP_ID_ARG)
        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.secondary) { viewModel.refreshDataSettlements() }
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
        viewModel.settlementsList.observe(viewLifecycleOwner) { settlement ->
            when (settlement) {
                is Resource.Success -> {
                    if(settlement.data.isEmpty()) { binding.textEmptyList.setVisible() } else binding.textEmptyList.setGone()
                    binding.layoutRefresh.isRefreshing = false
                    val userSettlements = settlement.data.filter { it.debtee.id == PLACEHOLDER_USERID || it.debtor.id == PLACEHOLDER_USERID }
                    val otherSettlements = settlement.data.minus(userSettlements.toSet())
                    userAdapter.submitList(userSettlements)
                    otherAdapter.submitList(otherSettlements)
                }
                is Resource.Loading -> {
                    binding.textEmptyList.setGone()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
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
        userAdapter.setPopupMenu(popupMenu)
        userAdapter.setCurrency(currency)
        otherAdapter.setCurrency(currency)

        val concatAdapter = ConcatAdapter(userAdapter, otherAdapter)
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
    override fun onMenuResolve(settlement: Settlement) {
        val resolveDialog = ResolveSettlementDialog(this, settlement)
        resolveDialog.show(childFragmentManager, ResolveSettlementDialog.TAG)
    }

    //dialogs
    override fun onResolveClick(settlement: Settlement) {
        requireContext().toast("resolve")
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1L
        private const val GROUP_ID_ARG = "groupId"
    }

}