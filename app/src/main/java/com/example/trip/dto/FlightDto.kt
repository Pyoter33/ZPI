package com.example.trip.dto

import java.time.Duration
import java.time.LocalDateTime

data class FlightDto(
    val flightId: Long,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val flightDuration: Duration,
    val travelToAirportDuration: Duration,
    val travelToAccommodationDuration: Duration,
    val airTransport: AirTransportDto
)