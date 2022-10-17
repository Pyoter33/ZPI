package com.example.trip.models

data class Participant(
    val id: Int,
    val groupId: Int,
    val fullName: String,
    val email: String,
    val role: UserRole
)