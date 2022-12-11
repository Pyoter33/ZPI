package com.example.trip.dto

import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class UserTransportPostDto(
    val duration: Duration,
    val price: BigDecimal,
    val source: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val meanOfTransport: String,
    val description: String?,
    val meetingTime: LocalDateTime,
    val link: String
)