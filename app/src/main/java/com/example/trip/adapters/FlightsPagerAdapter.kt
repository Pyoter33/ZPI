package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemFlightBinding
import com.example.trip.models.Flight
import com.example.trip.utils.toStringTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FlightsPagerAdapter @Inject constructor(): ListAdapter<Flight, FlightsPagerAdapter.FlightsViewHolder>(FlightsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightsViewHolder {
        return FlightsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FlightsViewHolder(
        private val binding: ItemFlightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(flight: Flight) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            with(binding) {
                textFlightNumber.text = flight.flightNumber
                textSource.text = flight.departureAirport
                textSourceHour.text = flight.departureTime.format(formatter)
                textDuration.text = flight.duration.toStringTime()
                textDestination.text = flight.arrivalAirport
                textDestinationHour.text = flight.arrivalTime.format(formatter)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup
            ): FlightsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFlightBinding.inflate(layoutInflater, parent, false)
                return FlightsViewHolder(
                    binding
                )
            }
        }

    }
}

class FlightsDiffUtil : DiffUtil.ItemCallback<Flight>() {
    override fun areItemsTheSame(
        oldItem: Flight,
        newItem: Flight
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: Flight,
        newItem: Flight
    ): Boolean {
        return oldItem.id == newItem.id
    }
}
