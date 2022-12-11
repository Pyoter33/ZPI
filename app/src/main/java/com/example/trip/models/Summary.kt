package com.example.trip.models

data class Summary(
    val accommodation: Accommodation?,
    val availability: OptimalAvailability?,
    val participants: List<Participant>
)