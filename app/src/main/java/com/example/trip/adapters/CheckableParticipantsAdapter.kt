package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemCheckableParticipantBinding
import com.example.trip.models.CheckableParticipant
import com.example.trip.utils.toStringFormat
import com.google.android.material.checkbox.MaterialCheckBox
import javax.inject.Inject


class CheckableParticipantsAdapter @Inject constructor() :
    ListAdapter<CheckableParticipant, CheckableParticipantsAdapter.CheckableParticipantViewHolder>(CheckableParticipantDiffUtil()) {

    private lateinit var checkableParticipantClickListener: CheckableParticipantClickListener

    private lateinit var currency: String

    fun setOnClickListener(checkableParticipantClickListener: CheckableParticipantClickListener) {
        this.checkableParticipantClickListener = checkableParticipantClickListener
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableParticipantViewHolder {
        return CheckableParticipantViewHolder.create(parent,currency, checkableParticipantClickListener)
    }

    override fun onBindViewHolder(holder: CheckableParticipantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CheckableParticipantViewHolder(
        private val binding: ItemCheckableParticipantBinding,
        private val currency: String,
        private val checkableParticipantClickListener: CheckableParticipantClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(checkableParticipant: CheckableParticipant) {
            with(binding) {
                textFullName.text = checkableParticipant.participant.fullName
                textPrice.text = checkableParticipant.amount.toStringFormat(currency)
                checkboxParticipant.isChecked = checkableParticipant.isChecked
            }
            onCheckClick()
        }

        private fun onCheckClick() {
            binding.checkboxParticipant.setOnClickListener {
                checkableParticipantClickListener.onCheck(bindingAdapterPosition, (it as MaterialCheckBox).isChecked)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String,
                checkableParticipantClickListener: CheckableParticipantClickListener
            ): CheckableParticipantViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCheckableParticipantBinding.inflate(layoutInflater, parent, false)
                return CheckableParticipantViewHolder(
                    binding,
                    currency,
                    checkableParticipantClickListener
                )
            }
        }

    }
}

class CheckableParticipantDiffUtil : DiffUtil.ItemCallback<CheckableParticipant>() {
    override fun areItemsTheSame(oldItem: CheckableParticipant, newItem: CheckableParticipant): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: CheckableParticipant, newItem: CheckableParticipant): Boolean {
        return oldItem.participant.id == newItem.participant.id
    }
}

interface CheckableParticipantClickListener {
    fun onCheck(position: Int, isChecked: Boolean)
}