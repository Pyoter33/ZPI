package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemParticipantPriceBinding
import com.example.trip.models.CheckableParticipant
import com.example.trip.utils.toStringFormat
import javax.inject.Inject


class ParticipantsPriceAdapter @Inject constructor() :
    ListAdapter<CheckableParticipant, ParticipantsPriceAdapter.ParticipantPriceViewHolder>(
        CheckableParticipantDiffUtil()
    ) {

    private lateinit var currency: String
    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantPriceViewHolder {
        return ParticipantPriceViewHolder.create(parent, currency)
    }

    override fun onBindViewHolder(holder: ParticipantPriceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ParticipantPriceViewHolder(
        private val binding: ItemParticipantPriceBinding,
        private val currency: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(checkableParticipant: CheckableParticipant) {
            with(binding) {
                textFullName.text = checkableParticipant.participant.fullName
                textPrice.text = checkableParticipant.amount.toStringFormat(currency)
            }
        }


        companion object {
            fun create(
                parent: ViewGroup,
                currency: String
            ): ParticipantPriceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemParticipantPriceBinding.inflate(layoutInflater, parent, false)
                return ParticipantPriceViewHolder(
                    binding,
                    currency
                )
            }
        }

    }
}