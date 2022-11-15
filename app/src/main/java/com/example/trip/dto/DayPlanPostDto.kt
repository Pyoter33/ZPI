package com.example.trip.dto

import java.time.LocalDate

data class DayPlanPostDto(
    val groupId: Long,
    val date: LocalDate,
    val name: String,
    val iconType: Int,
)