package com.example.trip.models

data class Summary(
    val groupId: Long,
    val accommodation: Accommodation?,
    val availability: Availability?,
    val participants: List<Participant>
)