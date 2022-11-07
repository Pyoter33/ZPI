package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemAvailabilityBinding
import com.example.trip.models.Availability
import com.example.trip.utils.setGone
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DatesListAdapter @Inject constructor() :
    ListAdapter<Availability, DatesListAdapter.DatesViewHolder>(DatesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesViewHolder {
        return DatesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DatesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DatesViewHolder(
        private val binding: ItemAvailabilityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(availability: Availability) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

            with(availability) {
                binding.textDateStart.text = startDate.format(formatter)
                binding.textDateEnd.text = endDate.format(formatter)
                binding.buttonDeleteDates.setGone()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup
            ): DatesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAvailabilityBinding.inflate(layoutInflater, parent, false)
                return DatesViewHolder(
                    binding
                )
            }
        }

    }
}