package com.example.trip.dto

import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

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

data class AirTransportDto(
    override val transportId: Long,
    override val duration: Duration,
    override val price: BigDecimal,
    override val source: String,
    override val destination: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    override val link: String,
    val flight: List<FlightDto>
): TransportDto {
    override val transportTypeJson: Int = 1
}

data class CarTransportDto(
    override val transportId: Long,
    override val duration: Duration,
    override val price: BigDecimal,
    override val source: String,
    override val destination: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    override val link: String,
    val distanceInKm: Long
): TransportDto {
    override val transportTypeJson: Int = 2
}

data class UserTransportDto(
    override val transportId: Long,
    override val duration: Duration,
    override val price: BigDecimal,
    override val source: String,
    override val destination: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    override val link: String,
    val meanOfTransport: String,
    val description: String?,
    val meetingTime: LocalDateTime
): TransportDto {
    override val transportTypeJson: Int = 3
}