package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trip.databinding.FragmentDayPlansBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class DayPlansFragment @Inject constructor() : Fragment() {

    private var groupId by Delegates.notNull<Int>()

    private lateinit var binding: FragmentDayPlansBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupId = requireActivity().intent.extras!!.getInt("groupId")
        binding.button.setOnClickListener {
            findNavController().navigate(
                DayPlansFragmentDirections.actionDayPlansFragmentToAttractionsFragment(
                    groupId,
                    -1
                )
            )
        }
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
        private const val GROUP_ID_ARG = "groupId"
    }
}