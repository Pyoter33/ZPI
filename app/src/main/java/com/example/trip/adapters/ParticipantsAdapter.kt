package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemParticipantBinding
import com.example.trip.models.Participant
import com.example.trip.models.UserRole
import com.example.trip.utils.formatPhone
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import javax.inject.Inject

class ParticipantsAdapter @Inject constructor() :
    ListAdapter<Participant, ParticipantsAdapter.ParticipantsViewHolder>(ParticipantsDiffUtil()) {

    private lateinit var participantsClickListener: ParticipantsClickListener

    fun setParticipantsClickListener(participantsClickListener: ParticipantsClickListener) {
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
        private val participantsClickListener: ParticipantsClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: Participant) {
            with(binding) {
                textFullName.text = participant.fullName
                textEmail.text = participant.email
                textPhone.text = participant.phoneNumber.formatPhone()
                if(participant.role == UserRole.COORDINATOR) {
                    imageCoordinator.setVisible()
                } else {
                    imageCoordinator.setGone()
                }

                setOnLongClick(participant)
                setOnCalendarClickListener(participant)
            }
        }

        private fun setOnCalendarClickListener(participant: Participant) {
            binding.buttonAvailability.setOnClickListener {
                participantsClickListener.onCalendarClick(participant)
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
                participantsClickListener: ParticipantsClickListener
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

class ParticipantsDiffUtil : DiffUtil.ItemCallback<Participant>() {
    override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Participant, newItem: Participant): Boolean {
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface ParticipantsClickListener {
    fun onCalendarClick(participant: Participant)
    fun onLongClick(participant: Participant, view: View)
}