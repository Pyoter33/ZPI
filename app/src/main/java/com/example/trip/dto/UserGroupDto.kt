package com.example.trip.dto

import java.time.LocalDate

data class UserGroupDto(
    val groupId: Long,
    val name: String,
    val currency: String,
    val description: String?,
    val votesLimit: Int,
    val startLocation: String,
    val startCity: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val latitude: Double,
    val longitude: Double
)