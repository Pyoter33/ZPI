package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemSettlementBinding
import com.example.trip.models.Settlement
import com.example.trip.models.SettlementStatus
import com.skydoves.balloon.Balloon
import javax.inject.Inject

class SettlementAdapter @Inject constructor() :
    ListAdapter<Settlement, SettlementAdapter.SettlementViewHolder>(
        SettlementDiffUtil()
    ) {

    private lateinit var settlementClickListener: SettlementClickListener

    private lateinit var popupMenu: Balloon

    fun setSettlementClickListener(settlementClickListener: SettlementClickListener) {
        this.settlementClickListener = settlementClickListener
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementViewHolder {
        return SettlementViewHolder.create(parent, settlementClickListener, popupMenu)
    }

    override fun onBindViewHolder(holder: SettlementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SettlementViewHolder(
        private val binding: ItemSettlementBinding,
        private val settlementClickListener: SettlementClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(settlement: Settlement) {
            val userId = 1L
            with(binding) {
                textDebtee.text = settlement.debtee.fullName
                textDebtor.text = settlement.debtor.fullName
                textPrice.text = settlement.amount.toString() + "PLN"
                if (settlement.status == SettlementStatus.PENDING) {
                    textStatus.text = itemView.resources.getString(R.string.text_pending)
                    layoutMain.setBackgroundColor(itemView.resources.getColor(R.color.white, null))
                } else {
                    textStatus.text = itemView.resources.getString(R.string.text_resolved)
                    layoutMain.setBackgroundColor(itemView.resources.getColor(R.color.grey200, null))
                }
            }
            setOnLongClick(settlement)
        }

        private fun setOnLongClick(settlement: Settlement) {
            binding.root.setOnLongClickListener {
                popupMenu.showAlignBottom(binding.root)
                setOnPopupButtonClick(R.id.button_edit){settlementClickListener.onMenuResolve(settlement)}
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
                settlementClickListener: SettlementClickListener,
                popupMenu: Balloon
            ): SettlementViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSettlementBinding.inflate(layoutInflater, parent, false)
                return SettlementViewHolder(
                    binding,
                    settlementClickListener,
                    popupMenu
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
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface SettlementClickListener {
    fun onMenuResolve(settlement: Settlement)
}