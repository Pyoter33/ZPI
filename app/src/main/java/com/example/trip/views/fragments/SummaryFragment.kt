package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trip.R
import com.example.trip.adapters.ParticipantsSummaryAdapter
import com.example.trip.databinding.FragmentSummaryBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Availability
import com.example.trip.utils.getIntFromBundle
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class SummaryFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentSummaryBinding

    @Inject
    lateinit var adapter: ParticipantsSummaryAdapter

    //private val viewModel: SummaryViewModel by viewModels()

    private var groupId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = getIntFromBundle("groupId")
        setAdapter()
        observeSummary()
        onBackArrowClick()
    }


    private fun setAdapter() {
        binding.listParticipants.adapter = adapter
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {

        }
    }

    private fun observeSummary() {
//        viewModel.summary.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Success -> {
//                    adapter.submitList(it.data.participants)
//                    it.data.availability?.let { availability ->
//                        setDate(availability)
//                    } ?: hideDate()
//
//                    it.data.accommodation?.let { accommodation ->
//                        setAccommodation(accommodation)
//                    } ?: hideAccommodation()
//
//                }
//                is Resource.Loading -> {
//
//                }
//                is Resource.Failure -> {
//                    requireContext().toast(R.string.text_fetch_failure)
//                }
//            }
//        }
    }

    private fun setAccommodation(accommodation: Accommodation) {
        with(binding) {
            textAccommodationNotAccepted.setGone()
            cardAccommodation.setVisible()
            imageAccommodationStatus.isActivated = true
            buttonUncheckAccommodation.setVisible()

            textName.text = accommodation.name
            textAddress.text = accommodation.address
            textVotes.text = accommodation.votes.toString()
            textPrice.text = getString(R.string.text_pln, accommodation.price.toString())
            textDescription.text = accommodation.description

            buttonLink.setOnClickListener {

            }

            buttonTransport.setOnClickListener {

            }
        }
    }

    private fun hideAccommodation() {
        with(binding) {
            textAccommodationNotAccepted.setVisible()
            cardAccommodation.setGone()
            imageAccommodationStatus.isActivated = false
            buttonUncheckAccommodation.setGone()
        }
    }

    private fun setDate(availability: Availability) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        with(binding) {
            textDatesNotAccepted.setGone()
            imageDatesStatus.isActivated = true
            buttonUncheckDates.setVisible()
            editTextDate.setText(
                getString(
                    R.string.format_dash,
                    availability.startDate.format(formatter),
                    availability.endDate.format(formatter)
                )
            )
        }
    }

    private fun hideDate() {
        with(binding) {
            textDatesNotAccepted.setVisible()
            imageDatesStatus.isActivated = false
            buttonUncheckDates.setGone()
        }
    }

}