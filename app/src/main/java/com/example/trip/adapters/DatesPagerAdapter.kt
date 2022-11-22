package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemOptimalAvailabilityBinding
import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DatesPagerAdapter @Inject constructor() :
    ListAdapter<OptimalAvailability, DatesPagerAdapter.DatesPagerViewHolder>(DatesPagerDiffUtil()) {

    private lateinit var datesClickListener: DatesPagerClickListener

    var showAccept = true

    fun setDatesClickListener(datesClickListener: DatesPagerClickListener) {
        this.datesClickListener = datesClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesPagerViewHolder {
        return DatesPagerViewHolder.create(parent, showAccept, datesClickListener)
    }

    override fun onBindViewHolder(holder: DatesPagerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DatesPagerViewHolder(
        private val binding: ItemOptimalAvailabilityBinding,
        private val showAccept: Boolean,
        private val datesClickListener: DatesPagerClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(availability: OptimalAvailability) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

            with(binding) {
                textDates.text = itemView.resources.getString(
                    R.string.format_dash,
                    availability.availability.startDate.format(formatter),
                    availability.availability.endDate.format(formatter)
                )
                textParticipantsNo.text = availability.users.toString()
                textDaysNo.text = itemView.resources.getString(R.string.format_days, availability.days)
                if(showAccept) buttonAcceptDates.setVisible() else buttonAcceptDates.setGone()
            }

            setOnAcceptClickListener(availability.availability)
        }

        private fun setOnAcceptClickListener(availability: Availability) {
            binding.buttonAcceptDates.setOnClickListener {
                datesClickListener.onAcceptClick(availability)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                showAccept: Boolean,
                datesClickListener: DatesPagerClickListener
            ): DatesPagerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOptimalAvailabilityBinding.inflate(layoutInflater, parent, false)
                return DatesPagerViewHolder(
                    binding,
                    showAccept,
                    datesClickListener
                )
            }
        }

    }
}

class DatesPagerDiffUtil : DiffUtil.ItemCallback<OptimalAvailability>() {
    override fun areItemsTheSame(
        oldItem: OptimalAvailability,
        newItem: OptimalAvailability
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: OptimalAvailability,
        newItem: OptimalAvailability
    ): Boolean {
        return oldItem.availability == newItem.availability
    }
}

interface DatesPagerClickListener {
    fun onAcceptClick(availability: Availability)
}