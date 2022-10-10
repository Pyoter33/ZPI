package com.example.trip.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.trip.activities.MainActivity
import com.example.trip.databinding.FragmentGroupsListBinding
import com.example.trip.models.TripStatus
import com.example.trip.viewmodels.GroupsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupsListFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentGroupsListBinding

    private val viewModel: GroupsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onButtonPreTripClick()
        onButtonTripClick()
    }

    private fun onButtonPreTripClick() {
        binding.buttonPretrip.setOnClickListener {
            val activityIntent = Intent(requireContext(), MainActivity::class.java)
            activityIntent.putExtra("status", TripStatus.PRE_TRIP.code)
            startActivity(activityIntent)
            requireActivity().finish()
        }
    }

    private fun onButtonTripClick() {
        binding.buttonTrip.setOnClickListener {
            val activityIntent = Intent(requireContext(), MainActivity::class.java)
            activityIntent.putExtra("status", TripStatus.TRIP.code)
            activityIntent.putExtra("groupId", -15)
            startActivity(activityIntent)
            requireActivity().finish()
        }
    }

}