package com.example.trip.models

import java.time.Duration
import java.time.LocalTime

data class Flight(
    val id: Long,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: LocalTime,
    val arrivalTime: LocalTime,
    val duration: Duration,
    val travelToAirportDuration: Duration?,
    val travelToAccommodationDuration: Duration?
)