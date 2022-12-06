package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ParticipantsPriceAdapter
import com.example.trip.databinding.FragmentExpenseDetailsBinding
import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.finances.ExpenseDetailsViewModel
import com.example.trip.views.dialogs.accommodation.DeleteAccommodationDialog
import com.example.trip.views.dialogs.finances.DeleteExpenseDialog
import com.example.trip.views.dialogs.finances.DeleteExpenseDialogClickListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseDetailsFragment @Inject constructor() : BaseFragment<FragmentExpenseDetailsBinding>(), DeleteExpenseDialogClickListener {

    private val viewModel: ExpenseDetailsViewModel by viewModels()

    private val args: ExpenseDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var adapter: ParticipantsPriceAdapter

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentExpenseDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArgs()
        setAdapter()
        onEditClick()
        onDeleteButtonClick(args.expense)
        onBackArrowClick()
        observeParticipants()
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setCurrency(args.currency)
        binding.listParticipants.adapter = adapter
        binding.listParticipants.layoutManager = layoutManager
        binding.listParticipants.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )
    }

    private fun setupArgs() {
        with(args) {
            if(args.expense.creator.id != preferencesHelper.getUserId()) {
                binding.buttonEdit.setGone()
                binding.buttonDelete.setGone()
            }
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            binding.textDate.text = expense.creationDate.format(formatter)
            binding.textExpenseName.text = expense.title
            binding.textPrice.text = expense.price.toStringFormat(currency)
            viewModel.setParticipants(expense.debtors, expense.price)
        }
    }

    private fun onEditClick() {
        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(
                ExpenseDetailsFragmentDirections.actionExpenseDetailsFragmentToCreateEditExpenseFragment(
                    args.groupId,
                    args.currency,
                    args.expense
                )
            )
        }
    }

    private fun onDeleteButtonClick(expense: Expense) {
        binding.buttonDelete.setOnClickListener {
            val deleteDialog = DeleteExpenseDialog(this, expense)
            deleteDialog.show(childFragmentManager, DeleteAccommodationDialog.TAG)
        }
    }

    //dialogs
    override fun onDeleteClick(expense: Expense) {
        lifecycleScope.launch {
            when (viewModel.deleteExpenseAsync().await()) {
                is Resource.Success -> {
                    binding.layoutLoading.setGone()
                    findNavController().popBackStackWithRefresh()
                }
                is Resource.Loading -> {
                    binding.layoutLoading.setVisible()
                }
                is Resource.Failure -> {
                    binding.layoutLoading.setGone()
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        onDeleteClick(expense)
                    }
                }
            }

        }
    }

    private fun observeParticipants() {
        viewModel.participants.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}