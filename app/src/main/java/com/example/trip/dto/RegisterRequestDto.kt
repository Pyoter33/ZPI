package com.example.trip.dto

import java.time.LocalDate

data class RegisterRequestDto(
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val surname: String,
    val birthday: LocalDate
)
