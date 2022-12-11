package com.example.trip.dto

data class AttractionCandidateDto(
    val attractionName: String,
    val address: String,
    val openingHours: Array<String>?,
    val latitude: Double,
    val longitude: Double,
    val placeId: String,
    val photoLink: String?,
    val url: String,
    val description: String?
)
