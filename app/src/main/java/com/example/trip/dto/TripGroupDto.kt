package com.example.trip.dto

import java.time.LocalDate
import java.util.*


enum class GroupStage {
    PLANNING_STAGE, TRIP_STAGE, AFTER_TRIP_STAGE
}


data class TripGroupDto(
    val groupId: Long,
    val name: String,
    val currency: Currency,
    val description: String?,
    val startLocation: String?,
    val startCity: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val latitude: Double,
    val longitude: Double,
    val groupStage: GroupStage,
    val minimalNumberOfDays: Int,
    val minimalNumberOfParticipants: Int,
    val selectedAccommodationId: Long?
    )
