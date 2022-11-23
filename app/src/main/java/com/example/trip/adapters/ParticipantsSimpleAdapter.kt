package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemParticipantBinding
import com.example.trip.models.Participant
import com.example.trip.models.UserRole
import com.example.trip.utils.formatPhone
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import javax.inject.Inject

class ParticipantsSimpleAdapter @Inject constructor() :
    ListAdapter<Participant, ParticipantsSimpleAdapter.ParticipantsViewHolder>(ParticipantsDiffUtil()) {

    private lateinit var participantsClickListener: ParticipantsSimpleClickListener

    fun setParticipantsClickListener(participantsClickListener: ParticipantsSimpleClickListener) {
        this.participantsClickListener = participantsClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        return ParticipantsViewHolder.create(parent, participantsClickListener)
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ParticipantsViewHolder(
        private val binding: ItemParticipantBinding,
        private val participantsClickListener: ParticipantsSimpleClickListener
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: Participant) {
            with(binding) {
                textFullName.text = participant.fullName
                textEmail.text = participant.email
                textPhone.text = participant.phoneNumber.formatPhone()
                buttonAvailability.setGone()
                if(participant.role == UserRole.COORDINATOR) {
                    imageCoordinator.setVisible()
                } else {
                    imageCoordinator.setGone()
                }

                setOnLongClick(participant)
            }
        }

        private fun setOnLongClick(participant: Participant) {
            binding.card.setOnLongClickListener {
                participantsClickListener.onLongClick(participant, it)
                true
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                participantsClickListener: ParticipantsSimpleClickListener
            ): ParticipantsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemParticipantBinding.inflate(layoutInflater, parent, false)
                return ParticipantsViewHolder(
                    binding,
                    participantsClickListener
                )
            }
        }

    }

}

interface ParticipantsSimpleClickListener {
    fun onLongClick(participant: Participant, view: View)
}