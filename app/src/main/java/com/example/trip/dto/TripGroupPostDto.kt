package com.example.trip.dto

import java.util.*

data class TripGroupPostDto(
    val name: String,
    val currency: Currency,
    val description: String? = null,
    val votesLimit: Int,
    val startLocation: String?,
    val startCity: String,
    val minimalNumberOfDays: Int,
    val minimalNumberOfParticipants: Int
)