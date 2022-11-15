package com.example.trip.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration

sealed interface TransportDto {
    val transportId: Long
    val duration: Duration
    val price: BigDecimal
    val source: String
    val destination: String
    val startDate: LocalDate
    val endDate: LocalDate
    val link: String
    val transportTypeJson: Int
}

data class AirTransport(
    override val transportId: Long,
    override val duration: Duration,
    override val price: BigDecimal,
    override val source: String,
    override val destination: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    override val link: String,
    override val transportTypeJson: Int,
    val flight: List<FlightDto>
): TransportDto

data class CarTransport(
    override val transportId: Long,
    override val duration: Duration,
    override val price: BigDecimal,
    override val source: String,
    override val destination: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    override val link: String,
    override val transportTypeJson: Int,
    val distanceInKm: Long
): TransportDto

data class UserTransport(
    override val transportId: Long,
    override val duration: Duration,
    override val price: BigDecimal,
    override val source: String,
    override val destination: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    override val link: String,
    override val transportTypeJson: Int,
    val meanOfTransport: String,
    val description: String?,
    val meetingTime: LocalDateTime
): TransportDto