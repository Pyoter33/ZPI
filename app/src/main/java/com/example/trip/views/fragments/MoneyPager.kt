package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trip.adapters.FragmentPagerAdapter
import com.example.trip.databinding.FragmentMoneyPagerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoneyPager @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentMoneyPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoneyPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    fun switchToFinancesFragment() {
        binding.viewPager.setCurrentItem(FINANCES_FRAGMENT_ID, true)
    }

    fun switchToSettlementsFragment() {
        binding.viewPager.setCurrentItem(SETTLEMENTS_FRAGMENT_ID, true)
    }

    private fun setupViewPager() {
        val adapter = FragmentPagerAdapter(this)
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