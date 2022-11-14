package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemSettlementBinding
import com.example.trip.databinding.ItemSettlementHeaderBinding
import com.example.trip.models.Settlement
import com.example.trip.models.SettlementStatus
import com.example.trip.utils.toStringFormat
import javax.inject.Inject

class SettlementOtherAdapter @Inject constructor() :
    ListAdapter<Settlement, RecyclerView.ViewHolder>(
        SettlementDiffUtil()
    ) {

    private lateinit var currency: String

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == HEADER) 0 else 1
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> SettlementHeaderViewHolder.create(parent)
            else -> SettlementViewHolder.create(parent, currency)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SettlementHeaderViewHolder -> holder.bind()
            is SettlementViewHolder -> holder.bind(getItem(position - 1))
        }
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


    class SettlementHeaderViewHolder(
        private val binding: ItemSettlementHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.textHeader.text = itemView.resources.getString(R.string.text_other_settlements)
        }

        companion object {
            fun create(
                parent: ViewGroup,
            ): SettlementHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSettlementHeaderBinding.inflate(layoutInflater, parent, false)
                return SettlementHeaderViewHolder(
                    binding
                )
            }
        }
    }


    companion object {
        const val HEADER = 0
        const val SETTLEMENT = 1
    }

}
