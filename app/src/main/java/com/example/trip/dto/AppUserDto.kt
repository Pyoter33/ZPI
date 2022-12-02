package com.example.trip.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class AppUserDto(
    val userId: Long,
    val phoneNumber: String,
    val email: String,
    val firstName: String,
    val surname: String,
    val birthday: LocalDate,
    val registrationDate: LocalDateTime
)