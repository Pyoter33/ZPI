package com.example.trip.models

import java.time.Duration

data class CarTransport(
    val transportId: Long,
    val duration: Duration,
    val sourceLatLng: String,
    val destinationLatLng: String,
    val distance: Long
)
