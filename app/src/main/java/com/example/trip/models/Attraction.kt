package com.example.trip.models

data class Attraction(
    val id: Int,
    val groupId: Int,
    val dayPlanId: Int,
    val name: String,
    val address: String,
    val description: String,
    val imageUrl: String,
    val link: String,
    val distanceToNext: Double,
    var isExpanded: Boolean = false
)