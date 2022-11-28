package com.example.trip.dto

import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.util.*


enum class GroupStage {
    PLANNING_STAGE, TRIP_STAGE, AFTER_TRIP_STAGE
}

@JsonClass(generateAdapter = true)
data class TripGroupDto(
    val name: String,
    val currency: Currency,
    val description: String? = null,
    val votesLimit: Int,
    val startLocation: String,
    val startCity: String,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val latitude: Double,
    val longitude: Double,
    val groupStage: GroupStage,
    val minimalNumberOfDays: Int,
    val minimalNumberOfParticipants: Int,
    val selectedAccommodationId: Long? = null,
    val selectedSharedAvailability: Long? = null,
    )
