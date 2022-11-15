package com.example.trip.dto

import java.time.LocalDate

data class SharedGroupAvailabilityDto(
    val sharedGroupAvailabilityId: Long,
    val userId: Long,
    val groupId: Long,
    val usersList: List<Long>,
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val numberOfDays: Int
)
