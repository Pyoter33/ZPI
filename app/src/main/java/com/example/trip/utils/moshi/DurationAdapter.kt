package com.example.trip.utils.moshi

import com.example.trip.utils.toStringTime
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Duration

class DurationAdapter {

    @FromJson
    fun decode(durationString: String): Duration {
        return Duration.parse(durationString)
    }

    @ToJson
    fun encode(durationDuration: Duration): String {
        return durationDuration.toStringTime()
    }

}