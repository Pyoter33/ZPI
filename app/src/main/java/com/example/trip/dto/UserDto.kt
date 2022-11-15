package com.example.trip.dto

import java.time.LocalDate

data class UserDto(
    val userId: Long,
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val surname: String,
    val birthday: LocalDate
)