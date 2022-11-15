package com.example.trip.dto

import java.math.BigDecimal

data class AccommodationDto(
    val accommodationId: Long,
    val groupId: Long,
    val creatorId: Long,
    val name: String,
    val streetAddress: String,
    val city: String,
    val country: String,
    val region: String,
    val description: String?,
    val imageLink: String,
    val sourceLink: String,
    val givenVotes: Int,
    val price: BigDecimal,
    val latitude: Double,
    val longitude: Double
)