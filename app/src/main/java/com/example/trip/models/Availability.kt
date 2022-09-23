package com.example.trip.models

import java.time.LocalDate

data class Availability (val id: Int, val userId: Int, val startDate: LocalDate, val endDate: LocalDate)