package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.adapters.FragmentPagerAdapter
import com.example.trip.databinding.FragmentMoneyPagerBinding
import com.example.trip.models.Expense
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoneyPager @Inject constructor() : BaseFragment<FragmentMoneyPagerBinding>() {

    private val args: MoneyPagerArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMoneyPagerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    fun navigateToExpenseDetailsFragment(expense: Expense) {
        findNavController().navigate(
            MoneyPagerDirections.actionMoneyPagerToExpenseDetailsFragment(
                args.groupId,
                args.currency,
                expense
            )
        )
    }

    fun navigateToCreateEditFragment(expense: Expense? = null) {
        findNavController().navigate(
            MoneyPagerDirections.actionFinancesToCreateEditExpenseFragment(
                args.groupId,
                args.currency,
                expense
            )
        )
    }

    fun switchToFinancesFragment() {
        binding.viewPager.setCurrentItem(FINANCES_FRAGMENT_ID, true)
    }

    fun switchToSettlementsFragment() {
        binding.viewPager.setCurrentItem(SETTLEMENTS_FRAGMENT_ID, true)
    }

    private fun setupViewPager() {
        val adapter = FragmentPagerAdapter(this, args.toBundle())
        adapter.apply {
            addNewFragment(FinancesFragment())
            addNewFragment(SettlementsFragment())
        }
        binding.viewPager.adapter = adapter
    }

    companion object {
        private const val FINANCES_FRAGMENT_ID = 0
        private const val SETTLEMENTS_FRAGMENT_ID = 1
    }

}