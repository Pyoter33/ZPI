package com.example.trip.models

data class Summary(
    val groupId: Int,
    val accommodation: Accommodation?,
    val availability: Availability?,
    val participants: List<Participant>
)