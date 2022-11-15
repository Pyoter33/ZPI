package com.example.trip.views.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.adapters.ParticipantsSummaryAdapter
import com.example.trip.databinding.FragmentSummaryBinding
import com.example.trip.databinding.LayoutPdfBinding
import com.example.trip.models.Accommodation
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.SummaryViewModel
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class TripSummaryFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentSummaryBinding

    @Inject
    lateinit var adapter: ParticipantsSummaryAdapter

    private val viewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        observeAccommodation()
        observeAvailability()
        observeParticipants()
        disableLayout()
        onSaveClick()
        requireActivity().onBackArrowClick(binding.buttonBack)
        observeButtonLock()
    }

    private fun disableLayout() {
        binding.editTextDate.isClickable = false
        binding.buttonStartTrip.setGone()
        binding.textFieldDate.isHelperTextEnabled = false
        binding.buttonLock.setInvisible()

        val layoutParams = binding.listParticipants.layoutParams
        (layoutParams as ConstraintLayout.LayoutParams).apply {
            constrainedHeight = false
            matchConstraintMaxHeight = ConstraintLayout.LayoutParams.WRAP_CONTENT
        }
        binding.listParticipants.layoutParams = layoutParams
    }

    private fun setAdapter() {
        binding.listParticipants.adapter = adapter
    }


    private fun observeButtonLock() {
        viewModel.isButtonUnlocked.observe(viewLifecycleOwner) {
            binding.buttonStartTrip.isEnabled = it
        }
    }

    private fun onSaveClick() {
        binding.buttonSave.setOnClickListener {
            setPdf()
        }
    }

    private fun observeAccommodation() {
        viewModel.acceptedAccommodation.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { accommodation ->
                        setAccommodation(accommodation)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun observeAvailability() {
        viewModel.acceptedAvailability.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { availability ->
                        setDate(availability)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun observeParticipants() {
        viewModel.participants.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.textParticipantsNo.text = it.data.size.toString()
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    requireContext().toast(R.string.text_fetch_failure)
                }
            }
        }
    }

    private fun setAccommodation(accommodation: Accommodation) {
        with(binding) {
            textAccommodationNotAccepted.setGone()
            cardAccommodation.setVisible()

            textName.text = accommodation.name
            textAddress.text = accommodation.address
            textPrice.text = getString(R.string.text_pln, accommodation.price.toString())
            textPrice.setInvisible()
            textVotes.setGone()
            ImageViewCompat.setImageTintList(binding.buttonVote, ColorStateList.valueOf(resources.getColor(R.color.grey400, null)))
            textDescription.text = accommodation.description

            Glide.with(this@TripSummaryFragment).load(accommodation.imageUrl).centerCrop().into(binding.imageAccommodation)

            buttonLink.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(accommodation.sourceUrl)
                )
                startActivity(intent)
            }

            buttonTransport.isEnabled = false
        }
    }

    private fun setDate(availability: Availability) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        with(binding) {
            textDatesNotAccepted.setGone()
            editTextDate.setText(
                getString(
                    R.string.format_dash,
                    availability.startDate.format(formatter),
                    availability.endDate.format(formatter)
                )
            )

        }
    }

    private fun setPdf() {
        val pdfBinding = LayoutPdfBinding.inflate(LayoutInflater.from(context))

        with(pdfBinding) {
            editTextDate.text = binding.editTextDate.text
            textName.text = binding.textName.text
            textAddress.text = binding.textAddress.text
            buttonVote.setInvisible()
            textVotes.setInvisible()
            textPrice.setInvisible()
            textParticipantsNo.text = binding.textParticipantsNo.text
            imageAccommodation.setImageDrawable(binding.imageAccommodation.drawable)
            textDescription.text = binding.textDescription.text
            listParticipants.adapter = adapter
        }

        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromView(pdfBinding.root)
            .setFileName(Constants.SUMMARY_FILE_NAME)
            .build(object : PdfGeneratorListener() {
                override fun onStartPDFGeneration() {
                    //NO-OP
                }

                override fun onFinishPDFGeneration() {
                    //NO-OP
                }

            })

    }

    companion object {
        private const val GROUP_ID_ARG = "groupId"
    }

}