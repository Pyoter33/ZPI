package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemAvailabilityBinding
import com.example.trip.models.Availability
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class DatesListAdapter @Inject constructor() :
    ListAdapter<Availability, DatesListAdapter.DatesViewHolder>(DatesDiffUtil()) {

    private lateinit var datesClickListener: DatesClickListener

    fun setDatesClickListener(accommodationClickListener: DatesClickListener) {
        this.datesClickListener = accommodationClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesViewHolder {
        return DatesViewHolder.create(parent, datesClickListener)
    }

    override fun onBindViewHolder(holder: DatesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DatesViewHolder(
        private val binding: ItemAvailabilityBinding,
        private val datesClickListener: DatesClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(availability: Availability) {
            val formatter = DateTimeFormatter.ofPattern(("dd.MM.yyyy"))

            with(availability) {
                binding.textDateStart.text = startDate.format(formatter)
                binding.textDateEnd.text = endDate.format(formatter)
            }
            setOnDeleteClick(availability.id)
        }

        private fun setOnDeleteClick(id: Int) {
            binding.buttonDeleteDates.setOnClickListener {
                datesClickListener.onDeleteClick(id)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                datesClickListener: DatesClickListener
            ): DatesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAvailabilityBinding.inflate(layoutInflater, parent, false)
                return DatesViewHolder(
                    binding,
                    datesClickListener
                )
            }
        }

    }

}

class DatesDiffUtil : DiffUtil.ItemCallback<Availability>() {
    override fun areItemsTheSame(oldItem: Availability, newItem: Availability): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Availability, newItem: Availability): Boolean {
        return oldItem.id == newItem.id && oldItem.userId == newItem.userId
    }
}

interface DatesClickListener {
    fun onDeleteClick(id: Int)
}