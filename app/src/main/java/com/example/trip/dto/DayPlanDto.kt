package com.example.trip.dto

import java.time.LocalDate

data class DayPlanDto(
    val dayPlanId: Long,
    val groupId: Long,
    val date: LocalDate,
    val name: String,
    val iconType: Int,
    val dayAttractions: Set<AttractionDto>,
    val dayPlanStartingPointId: Long?
)
