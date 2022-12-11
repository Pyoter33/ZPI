package com.example.trip.models

enum class UserRole(val id: Int) {
    BASIC_USER(0),
    COORDINATOR(1),
    ADMINISTRATOR(2),
    UNSPECIFIED(4)
}