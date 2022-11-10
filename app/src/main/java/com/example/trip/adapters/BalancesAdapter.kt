package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemBalanceMinusBinding
import com.example.trip.databinding.ItemBalancePlusBinding
import com.example.trip.models.Balance
import com.example.trip.models.BalanceStatus
import javax.inject.Inject
import kotlin.math.absoluteValue

class BalancesAdapter @Inject constructor() :
    ListAdapter<Balance, RecyclerView.ViewHolder>(
        BalancesDiffUtil()
    ) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).status.code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BalanceStatus.POSITIVE.code -> BalancePlusViewHolder.create(parent)
            else -> BalanceMinusViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BalancePlusViewHolder -> holder.bind(getItem(position))
            is BalanceMinusViewHolder -> holder.bind(getItem(position))
        }
    }

    class BalancePlusViewHolder(
        private val binding: ItemBalancePlusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(balance: Balance) {
            with(binding) {
                textName.text = balance.participant.fullName
                textPrice.text = "+" + balance.amount.toString() + " PLN"
                indicator.max = balance.maxAmount.toInt()
                indicator.progress = balance.amount.toInt()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup
            ): BalancePlusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBalancePlusBinding.inflate(layoutInflater, parent, false)
                return BalancePlusViewHolder(
                    binding
                )
            }
        }
    }

    class BalanceMinusViewHolder(
        private val binding: ItemBalanceMinusBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(balance: Balance) {
            with(binding) {
                textName.text = balance.participant.fullName
                textPrice.text = balance.amount.toString() + " PLN"
                indicator.max = balance.maxAmount.toInt().absoluteValue
                indicator.progress = balance.amount.toInt().absoluteValue
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
            ): BalanceMinusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBalanceMinusBinding.inflate(layoutInflater, parent, false)
                return BalanceMinusViewHolder(
                    binding
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

