package com.example.trip.models

import java.time.LocalDate

data class Availability (val id: Long, val userId: Long, val startDate: LocalDate, val endDate: LocalDate)