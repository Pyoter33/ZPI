package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemSettlementBinding
import com.example.trip.models.Settlement
import com.example.trip.models.SettlementStatus
import com.example.trip.utils.toStringFormat
import javax.inject.Inject

class SettlementOtherAdapter @Inject constructor() :
    ListAdapter<Settlement, SettlementOtherAdapter.SettlementViewHolder>(
        SettlementDiffUtil()
    ) {

    private lateinit var currency: String

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementViewHolder {
       return SettlementViewHolder.create(parent, currency)
    }

    override fun onBindViewHolder(holder: SettlementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SettlementViewHolder(
        private val binding: ItemSettlementBinding,
        private val currency: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(settlement: Settlement) {
            with(binding) {
                textDebtee.text = settlement.debtee.fullName
                textDebtor.text = settlement.debtor.fullName
                textPrice.text = settlement.amount.toStringFormat(currency)
                if (settlement.status == SettlementStatus.PENDING) {
                    textStatus.text = itemView.resources.getString(R.string.text_pending)
                } else {
                    textStatus.text = itemView.resources.getString(R.string.text_resolved)
                }
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String
            ): SettlementViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSettlementBinding.inflate(layoutInflater, parent, false)
                return SettlementViewHolder(
                    binding,
                    currency
                )
            }
        }
    }
}
