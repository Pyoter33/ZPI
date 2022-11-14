package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemParticipantBinding
import com.example.trip.models.Participant
import com.example.trip.models.UserRole
import com.example.trip.utils.formatPhone
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.skydoves.balloon.Balloon
import javax.inject.Inject

class ParticipantsSimpleAdapter @Inject constructor() :
    ListAdapter<Participant, ParticipantsSimpleAdapter.ParticipantsViewHolder>(ParticipantsDiffUtil()) {

    private lateinit var participantsClickListener: ParticipantsSimpleClickListener

    private lateinit var popupMenu: Balloon

    fun setParticipantsClickListener(participantsClickListener: ParticipantsSimpleClickListener) {
        this.participantsClickListener = participantsClickListener
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        return ParticipantsViewHolder.create(parent, participantsClickListener, popupMenu)
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ParticipantsViewHolder(
        private val binding: ItemParticipantBinding,
        private val participantsClickListener: ParticipantsSimpleClickListener,
        private val popupMenu: Balloon
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
                popupMenu.showAlignBottom(binding.card)
                setOnPopupButtonClick(R.id.button_coordinate) {
                    participantsClickListener.onMenuCoordinateClick(
                        participant
                    )
                }
                setOnPopupButtonClick(R.id.button_delete) {
                    participantsClickListener.onMenuDeleteClick(
                        participant
                    )
                }
                true
            }
        }

        private fun setOnPopupButtonClick(id: Int, action: () -> Unit) {
            popupMenu.getContentView().findViewById<Button>(id).setOnClickListener {
                action()
                popupMenu.dismiss()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                participantsClickListener: ParticipantsSimpleClickListener,
                popupMenu: Balloon
            ): ParticipantsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemParticipantBinding.inflate(layoutInflater, parent, false)
                return ParticipantsViewHolder(
                    binding,
                    participantsClickListener,
                    popupMenu
                )
            }
        }

    }

}

interface ParticipantsSimpleClickListener {
    fun onMenuCoordinateClick(participant: Participant)
    fun onMenuDeleteClick(participant: Participant)
}