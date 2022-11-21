package com.example.trip.dto


data class AttractionDto(
    val attractionId: Long,
    val name: String,
    val description: String?,
    val openingHours: String,
    val address: String,
    val attractionLink: String,
    val photoLink: String,
    val days: Set<DayPlanDto>,
    val latitude: Double,
    val longitude: Double
)

data class AttractionPlanDto(
    val attraction: AttractionDto,
    val distanceToNext: Double
)