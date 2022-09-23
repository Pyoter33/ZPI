package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trip.databinding.FragmentGroupsListBinding
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
            findNavController().navigate(GroupsListFragmentDirections.actionGroupsListFragmentToPreTrip(-1))
        }

    }

    private fun onButtonTripClick() {
        binding.buttonTrip.setOnClickListener {
            findNavController().navigate(GroupsListFragmentDirections.actionGroupsListFragmentToTrip())
        }

    }

}