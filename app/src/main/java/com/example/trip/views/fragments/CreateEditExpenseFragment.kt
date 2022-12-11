package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.trip.adapters.CheckableParticipantClickListener
import com.example.trip.adapters.CheckableParticipantsAdapter
import com.example.trip.databinding.FragmentCreateEditExpenseBinding
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.finances.CreateEditExpenseViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditExpenseFragment @Inject constructor() : BaseFragment<FragmentCreateEditExpenseBinding>(),
    CheckableParticipantClickListener {

    private val viewModel: CreateEditExpenseViewModel by viewModels()

    private val args: CreateEditExpenseFragmentArgs by navArgs()

    @Inject
    lateinit var adapter: CheckableParticipantsAdapter

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateEditExpenseBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArgs()
        setAdapter()
        setupOnLinkTextChangeListener()
        setupOnPriceTextChangeListener()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) {
            viewModel.refreshData()
            binding.checkboxCheckAll.isChecked = false
        }
        onSubmitClick()
        onBackArrowClick()
        setOnCheckAllClick()
        observeParticipants()
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setOnClickListener(this)
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
        args.expense?.let {
            binding.textNewExpense.text = getString(R.string.text_edit_expense)
            viewModel.toPost = false
            binding.editTextName.setText(it.title)
            binding.editTextPrice.setText(it.price.toStringFormat())
            viewModel.name = it.title
            viewModel.price = it.price.toString()
        }

    }

    private fun setupOnLinkTextChangeListener() {
        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.name = editable?.toString()
                binding.textFieldName.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldName.error = null
            }
        })
    }

    private fun setupOnPriceTextChangeListener() {
        binding.textFieldPrice.suffixText = args.currency
        binding.textFieldPrice.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.price = editable?.toString()
                binding.textFieldPrice.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldPrice.error = null
                binding.textError.setInvisible()
                adapter.currentList.forEachIndexed { index, it ->
                    onCheck(index, it.isChecked)
                }
            }
        })
    }

    private fun setOnCheckAllClick() {
        binding.checkboxCheckAll.setOnClickListener {
            viewModel.checkAll((it as MaterialCheckBox).isChecked)
            binding.textError.setInvisible()
            adapter.notifyDataSetChanged()
        }
    }

    private fun onSubmitClick() {
        binding.buttonSubmit.setOnClickListener {
            submit()
        }
    }

    private fun observeParticipants() {
        viewModel.participantsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.layoutRefresh.isRefreshing = false
                    args.expense?.let { expense ->
                        val size = viewModel.setCheckedParticipants(expense).size
                        if (size == it.data.size) binding.checkboxCheckAll.isChecked = true
                    }
                    adapter.submitList(it.data)
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refreshData()
                    }
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun submit() {
        if (isSubmitNotPermitted()) return
        enableLoading()

        val operation =
            if (viewModel.toPost) viewModel.postExpenseAsync() else viewModel.updateExpenseAsync()
        lifecycleScope.launch {
            when (operation.await()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStackWithRefresh(R.id.moneyPager, false)
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    disableLoading()
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        submit()
                    }
                }
            }

        }
    }

    private fun isSubmitNotPermitted(): Boolean {
        var showError = false

        with(binding) {
            if (viewModel.name.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.price.isNullOrEmpty()) {
                textFieldPrice.error = getString(R.string.text_text_empty)
                textFieldPrice.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.participantsList.value == null
                || viewModel.participantsList.value !is Resource.Success
                || (viewModel.participantsList.value as Resource.Success).data.all { !it.isChecked }
            ) {
                textError.setVisible()
            }
        }
        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldName.isEnabled = false
            textFieldPrice.isEnabled = false
            listParticipants.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldName.isEnabled = true
            textFieldPrice.isEnabled = true
            listParticipants.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

    override fun onCheck(position: Int, isChecked: Boolean) {
        val checkedParticipants = viewModel.checkParticipant(position, isChecked)
        adapter.notifyItemChanged(position)
        checkedParticipants.forEach {
            adapter.notifyItemChanged(it)
        }
        binding.textError.setInvisible()
        binding.checkboxCheckAll.isChecked = checkedParticipants.size == adapter.itemCount
    }

}