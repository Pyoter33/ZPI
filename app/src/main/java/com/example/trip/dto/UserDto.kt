package com.example.trip.dto

data class UserDto(
    val userId: Long,
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val surname: String
)