package com.example.trip.dto

import java.time.LocalDate
import java.util.*

data class UserGroupDto(
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
    val selectedAccommodationId: Long?,
    val selectedSharedAvailability: Long?,
)