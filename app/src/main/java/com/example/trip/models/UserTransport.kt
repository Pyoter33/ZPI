package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Parcelize
data class UserTransport(
    val id: Long,
    val groupId: Long,
    val accommodationId: Long,
    val meansOfTransport: List<String>,
    val duration: Duration,
    val meetingDate: LocalDate,
    val meetingTime: LocalTime,
    val price: BigDecimal,
    val source: String,
    val destination: String,
    val description: String?
): Parcelable