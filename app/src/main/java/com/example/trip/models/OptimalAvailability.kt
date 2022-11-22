package com.example.trip.models

data class OptimalAvailability(
    val availability: Availability,
    val users: Int,
    val days: Int
)