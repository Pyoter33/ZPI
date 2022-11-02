package com.example.trip.models

import java.time.Duration

class AirTransport(
    val transportId: Long,
    val duration: Duration,
    val source: String,
    val destination: String,
    val link: String,
    val flights: List<Flight>
)