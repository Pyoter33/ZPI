package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemParticipantSummaryBinding
import com.example.trip.models.Participant
import com.example.trip.models.UserRole
import com.example.trip.utils.formatPhone
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import javax.inject.Inject


class ParticipantsSummaryAdapter @Inject constructor() :
    ListAdapter<Participant, ParticipantsSummaryAdapter.ParticipantsSummaryViewHolder>(ParticipantsDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsSummaryViewHolder {
        return ParticipantsSummaryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ParticipantsSummaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ParticipantsSummaryViewHolder(
        private val binding: ItemParticipantSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: Participant) {
            with(binding) {
                textFullName.text = participant.fullName
                textEmail.text = participant.email
                textPhone.text = participant.phoneNumber.formatPhone()
                if (participant.role == UserRole.COORDINATOR) {
                    binding.imageCoordinator.setVisible()
                } else {
                    binding.imageCoordinator.setGone()
                }
            }

        }

        companion object {
            fun create(
                parent: ViewGroup
            ): ParticipantsSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemParticipantSummaryBinding.inflate(layoutInflater, parent, false)
                return ParticipantsSummaryViewHolder(
                    binding
                )
            }
        }

    }

}