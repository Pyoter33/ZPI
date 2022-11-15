package com.example.trip.dto

import java.math.BigDecimal

data class AccommodationPostDto(
    val groupId: Long,
    val creatorId: Long,
    val accommodationLink: String,
    val description: String,
    val price: BigDecimal
)