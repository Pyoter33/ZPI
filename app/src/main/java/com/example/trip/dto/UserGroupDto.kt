package com.example.trip.dto

import java.time.LocalDate
import java.util.*

data class UserGroupDto(
    val groupId: Long,
    val name: String,
    val currency: Currency,
    val description: String? = null,
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
    val participantsNum: Int
)