package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemBalanceMinusBinding
import com.example.trip.databinding.ItemBalancePlusBinding
import com.example.trip.models.Balance
import com.example.trip.models.BalanceStatus
import com.example.trip.utils.toStringFormat
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.absoluteValue

class BalancesAdapter @Inject constructor() :
    ListAdapter<Balance, RecyclerView.ViewHolder>(
        BalancesDiffUtil()
    ) {

    private lateinit var currency: String

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).status.code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BalanceStatus.POSITIVE.code -> BalancePlusViewHolder.create(parent, currency)
            BalanceStatus.NEUTRAL.code -> BalancePlusViewHolder.create(parent, currency)
            else -> BalanceMinusViewHolder.create(parent, currency)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BalancePlusViewHolder -> holder.bind(getItem(position))
            is BalanceMinusViewHolder -> holder.bind(getItem(position))
        }
    }

    class BalancePlusViewHolder(
        private val binding: ItemBalancePlusBinding,
        private val currency: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(balance: Balance) {
            with(binding) {
                textName.text = balance.participant.fullName
                textPrice.text =
                    if (balance.status == BalanceStatus.NEUTRAL) {
                        balance.amount.toStringFormat(currency)
                    } else {
                        itemView.resources.getString(
                            R.string.format_plus, balance.amount.toStringFormat(currency)
                        )
                    }
                indicator.max = balance.maxAmount.times(BigDecimal.valueOf(100)).toInt()
                indicator.progress = balance.amount.times(BigDecimal.valueOf(100)).toInt()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String
            ): BalancePlusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBalancePlusBinding.inflate(layoutInflater, parent, false)
                return BalancePlusViewHolder(
                    binding,
                    currency
                )
            }
        }
    }

    class BalanceMinusViewHolder(
        private val binding: ItemBalanceMinusBinding,
        private val currency: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(balance: Balance) {
            with(binding) {
                textName.text = balance.participant.fullName
                textPrice.text = balance.amount.toStringFormat(currency)
                indicator.max = balance.maxAmount.times(BigDecimal.valueOf(100)).toInt().absoluteValue
                indicator.progress = balance.amount.times(BigDecimal.valueOf(100)).toInt().absoluteValue
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String
            ): BalanceMinusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBalanceMinusBinding.inflate(layoutInflater, parent, false)
                return BalanceMinusViewHolder(
                    binding,
                    currency
                )
            }
        }
    }
}

class BalancesDiffUtil : DiffUtil.ItemCallback<Balance>() {
    override fun areItemsTheSame(oldItem: Balance, newItem: Balance): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Balance, newItem: Balance): Boolean {
        return oldItem.participant.id == newItem.participant.id
    }
}

