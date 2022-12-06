package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemSettlementBinding
import com.example.trip.models.Settlement
import com.example.trip.models.SettlementStatus
import com.example.trip.utils.toStringFormat
import javax.inject.Inject

class SettlementUserAdapter @Inject constructor() :
    ListAdapter<Settlement, SettlementUserAdapter.SettlementViewHolder>(
        SettlementDiffUtil()
    ) {

    private lateinit var currency: String

    private lateinit var settlementClickListener: SettlementClickListener

    fun setSettlementClickListener(settlementClickListener: SettlementClickListener) {
        this.settlementClickListener = settlementClickListener
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementViewHolder {
        return SettlementViewHolder.create(
            parent,
            currency,
            settlementClickListener
        )
    }

    override fun onBindViewHolder(holder: SettlementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SettlementViewHolder(
        private val binding: ItemSettlementBinding,
        private val currency: String,
        private val settlementClickListener: SettlementClickListener
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
            setOnLongClick(settlement)
        }

        private fun setOnLongClick(settlement: Settlement) {
            binding.root.setOnLongClickListener {
                settlementClickListener.onLongClick(settlement, it)
                true
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String,
                settlementClickListener: SettlementClickListener
            ): SettlementViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSettlementBinding.inflate(layoutInflater, parent, false)
                return SettlementViewHolder(
                    binding,
                    currency,
                    settlementClickListener
                )
            }
        }
    }

}

class SettlementDiffUtil : DiffUtil.ItemCallback<Settlement>() {
    override fun areItemsTheSame(oldItem: Settlement, newItem: Settlement): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Settlement, newItem: Settlement): Boolean {
        return oldItem.id == newItem.id && oldItem.status == newItem.status
    }
}

interface SettlementClickListener {
    fun onLongClick(settlement: Settlement, view: View)
}