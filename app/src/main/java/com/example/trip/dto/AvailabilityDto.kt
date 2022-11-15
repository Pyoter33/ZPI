package com.example.trip.dto

import java.time.LocalDate

data class AvailabilityDto(
    val availabilityId: Long,
    val userId: Long,
    val groupId: Long,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
)
