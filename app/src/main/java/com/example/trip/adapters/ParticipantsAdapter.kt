package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemParticipantBinding
import com.example.trip.models.Participant
import com.example.trip.models.UserRole
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.skydoves.balloon.Balloon
import javax.inject.Inject


class ParticipantsAdapter @Inject constructor() :
    ListAdapter<Participant, ParticipantsAdapter.ParticipantsViewHolder>(ParticipantsDiffUtil()) {

    private lateinit var participantsClickListener: ParticipantsClickListener

    private lateinit var popupMenu: Balloon

    fun setDayPlansClickListener(participantsClickListener: ParticipantsClickListener) {
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
        private val participantsClickListener: ParticipantsClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: Participant) {
            with(participant) { //fix
                binding.textFullName.text = fullName
                binding.textEmail.text = email
                if(role == UserRole.COORDINATOR) {
                    binding.imageCoordinator.setVisible()
                } else {
                    binding.imageCoordinator.setGone()
                }

                setOnLongClick(this)
                setOnCalendarClickListener(this)
            }
        }

        private fun setOnCalendarClickListener(participant: Participant) {
            binding.card.setOnClickListener {
                participantsClickListener.onCalendarClick(participant)
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
                participantsClickListener: ParticipantsClickListener,
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
    fun onMenuCoordinateClick(participant: Participant)
    fun onMenuDeleteClick(participant: Participant)
}