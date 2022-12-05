package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ParticipantsSimpleAdapter
import com.example.trip.adapters.ParticipantsSimpleClickListener
import com.example.trip.databinding.FragmentParticipantsBinding
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.utils.*
import com.example.trip.viewmodels.participants.ParticipantsViewModel
import com.example.trip.views.dialogs.MenuPopupCoordinateFactory
import com.example.trip.views.dialogs.participants.DeleteParticipantDialog
import com.example.trip.views.dialogs.participants.DeleteParticipantDialogClickListener
import com.example.trip.views.dialogs.participants.SetCoordinatorDialog
import com.example.trip.views.dialogs.participants.SetCoordinatorDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ParticipantsTripFragment @Inject constructor() : BaseFragment<FragmentParticipantsBinding>(), ParticipantsSimpleClickListener,
    DeleteParticipantDialogClickListener, SetCoordinatorDialogClickListener {

    @Inject
    lateinit var adapter: ParticipantsSimpleAdapter

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    private val popupMenu by balloon<MenuPopupCoordinateFactory>()

    private val viewModel: ParticipantsViewModel by viewModels()

    private val args: ParticipantsTripFragmentArgs by navArgs()

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentParticipantsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonInvite.setInvisible()
        setAdapter()
        observeParticipants()
        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        setOnSearchClickListener()
    }

    private fun setAdapter() {
        binding.listParticipants.adapter = adapter
        adapter.setParticipantsClickListener(this)
    }

    private fun observeParticipants() {
        viewModel.participantsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.layoutRefresh.isRefreshing = false
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

    private fun setOnSearchClickListener() {
        binding.editTextQuery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) adapter.submitList(viewModel.filterParticipants(""))
                if (count > 2) adapter.submitList(viewModel.filterParticipants(text.toString()))
            }
        })
    }

    override fun onLongClick(participant: Participant, view: View) {
        if(isCoordinator() && participant.role == UserRole.BASIC_USER) {
            popupMenu.setOnPopupButtonClick(R.id.button_coordinate) { onMenuCoordinateClick(participant) }
            popupMenu.setOnPopupButtonClick(R.id.button_delete) { onMenuDeleteClick(participant) }
            popupMenu.showAlignBottom(view)
        }
    }

    private fun isCoordinator() = preferencesHelper.getUserId() in args.coordinators

    private fun onMenuCoordinateClick(participant: Participant) {
        val setCoordinatorDialog = SetCoordinatorDialog(this, participant)
        setCoordinatorDialog.show(childFragmentManager, SetCoordinatorDialog.TAG)
    }

    private fun onMenuDeleteClick(participant: Participant) {
        val deleteDialog = DeleteParticipantDialog(this, participant)
        deleteDialog.show(childFragmentManager, DeleteParticipantDialog.TAG)
    }

    override fun onDeleteClick(participant: Participant) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.deleteParticipantAsync(participant.id).await()) {
                is Resource.Success -> {
                    viewModel.refreshData()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuDeleteClick(participant)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }

    override fun onCoordinateClick(participant: Participant) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.postCoordinatorAsync(participant.id).await()) {
                is Resource.Success -> {
                    viewModel.refreshData()
                    requireContext().toast(getString(R.string.text_is_now_coordinator, participant.fullName))
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuDeleteClick(participant)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
    }

}