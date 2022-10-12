package com.example.trip.models

import java.time.LocalDate

data class DayPlan(
    val id: Int,
    val groupId: Int,
    val name: String,
    val date: LocalDate,
    val attractions: List<Attraction>
)
