package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemSettlementHeaderBinding
import javax.inject.Inject

class SettlementHeaderAdapter @Inject constructor() :
    ListAdapter<String, SettlementHeaderAdapter.SettlementHeaderViewHolder>(
        StringDiffUtil()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementHeaderViewHolder {
        return SettlementHeaderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SettlementHeaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SettlementHeaderViewHolder(
        private val binding: ItemSettlementHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.textHeader.text = text
        }

        companion object {
            fun create(
                parent: ViewGroup
            ): SettlementHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSettlementHeaderBinding.inflate(layoutInflater, parent, false)
                return SettlementHeaderViewHolder(binding)
            }
        }
    }

}
