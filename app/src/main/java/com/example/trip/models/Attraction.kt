package com.example.trip.models

import java.time.LocalTime

data class Attraction(
    val id: Int,
    val groupId: Int,
    val dayPlanId: Int,
    val name: String,
    val address: String,
    val description: String,
    val openingHour: LocalTime,
    val closingHour: LocalTime,
    val imageUrl: String,
    val link: String,
    var isExpanded: Boolean = false
)