package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.ParticipantsSimpleAdapter
import com.example.trip.adapters.ParticipantsSimpleClickListener
import com.example.trip.databinding.FragmentParticipantsBinding
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.utils.onBackArrowClick
import com.example.trip.utils.setInvisible
import com.example.trip.utils.setSwipeRefreshLayout
import com.example.trip.viewmodels.participants.ParticipantsViewModel
import com.example.trip.views.dialogs.MenuPopupCoordinateFactory
import com.example.trip.views.dialogs.participants.DeleteParticipantDialog
import com.example.trip.views.dialogs.participants.DeleteParticipantDialogClickListener
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ParticipantsTripFragment @Inject constructor() : Fragment(), ParticipantsSimpleClickListener,
    DeleteParticipantDialogClickListener {

    private lateinit var binding: FragmentParticipantsBinding

    @Inject
    lateinit var adapter: ParticipantsSimpleAdapter

    private val popupMenu by balloon<MenuPopupCoordinateFactory>()

    private val viewModel: ParticipantsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonInvite.setInvisible()
        setAdapter()
        observeParticipants()
        requireActivity().onBackArrowClick(binding.buttonBack)
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
        setOnSearchClickListener()
        onBackArrowClick()
    }

    private fun setAdapter() {
        binding.listParticipants.adapter = adapter
        adapter.setPopupMenu(popupMenu)
        adapter.setParticipantsClickListener(this)
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
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

    override fun onMenuCoordinateClick(participant: Participant) {

    }

    override fun onMenuDeleteClick(participant: Participant) {
        val deleteDialog = DeleteParticipantDialog(this, participant)
        deleteDialog.show(childFragmentManager, DeleteParticipantDialog.TAG)
    }

    override fun onDeleteClick(participant: Participant) {

    }

}