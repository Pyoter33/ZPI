package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemOptimalAvailabilityBinding
import com.example.trip.models.Availability
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DatesPagerAdapter @Inject constructor() :
    ListAdapter<Pair<Availability, Int>, DatesPagerAdapter.DatesPagerViewHolder>(DatesPagerDiffUtil()) {

    private lateinit var datesClickListener: DatesPagerClickListener

    fun setDatesClickListener(datesClickListener: DatesPagerClickListener) {
        this.datesClickListener = datesClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesPagerViewHolder {
        return DatesPagerViewHolder.create(parent, datesClickListener)
    }

    override fun onBindViewHolder(holder: DatesPagerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DatesPagerViewHolder(
        private val binding: ItemOptimalAvailabilityBinding,
        private val datesClickListener: DatesPagerClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(availability: Pair<Availability, Int>) {
            val formatter = DateTimeFormatter.ofPattern(("dd.MM.yyyy"))

            with(binding) {
                textDates.text = itemView.resources.getString(
                    R.string.format_dash,
                    availability.first.startDate.format(formatter),
                    availability.first.endDate.format(formatter)
                )
                textParticipantsNo.text = availability.second.toString()
            }

            setOnAcceptClickListener(availability.first)
        }

        private fun setOnAcceptClickListener(availability: Availability) {
            binding.buttonAcceptDates.setOnClickListener {
                datesClickListener.onAcceptClick(availability)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                datesClickListener: DatesPagerClickListener
            ): DatesPagerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOptimalAvailabilityBinding.inflate(layoutInflater, parent, false)
                return DatesPagerViewHolder(
                    binding,
                    datesClickListener
                )
            }
        }

    }
}

class DatesPagerDiffUtil : DiffUtil.ItemCallback<Pair<Availability, Int>>() {
    override fun areItemsTheSame(
        oldItem: Pair<Availability, Int>,
        newItem: Pair<Availability, Int>
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: Pair<Availability, Int>,
        newItem: Pair<Availability, Int>
    ): Boolean {
        return oldItem.first == newItem.first
    }
}

interface DatesPagerClickListener {
    fun onAcceptClick(availability: Availability)
}