package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemSettlementBinding
import com.example.trip.databinding.ItemSettlementHeaderBinding
import com.example.trip.models.Settlement
import com.example.trip.models.SettlementStatus
import com.skydoves.balloon.Balloon
import javax.inject.Inject

class SettlementUserAdapter @Inject constructor() :
    ListAdapter<Settlement, RecyclerView.ViewHolder>(
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

    override fun getItemViewType(position: Int): Int {
        return if (position == HEADER) 0 else 1
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> SettlementHeaderViewHolder.create(parent)
            else -> SettlementViewHolder.create(parent, settlementClickListener, popupMenu)
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
                    layoutMain.setBackgroundColor(
                        itemView.resources.getColor(
                            R.color.grey200,
                            null
                        )
                    )
                }
            }

            if (settlement.debtee.id == userId && settlement.status != SettlementStatus.RESOLVED) {
                setOnLongClick(settlement)
            }
        }

        private fun setOnLongClick(settlement: Settlement) {
            binding.root.setOnLongClickListener {
                popupMenu.showAlignBottom(binding.root)
                setOnPopupButtonClick(R.id.button_resolve) {
                    settlementClickListener.onMenuResolve(
                        settlement
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


    class SettlementHeaderViewHolder(
        private val binding: ItemSettlementHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.textHeader.text = itemView.resources.getString(R.string.text_my_settlements)
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