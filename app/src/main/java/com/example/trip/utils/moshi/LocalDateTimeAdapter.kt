package com.example.trip.utils.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

class LocalDateTimeAdapter {

    @FromJson
    fun decode(dateString: String?): LocalDateTime? {
        return LocalDateTime.parse(dateString)
    }

    @ToJson
    fun encode(dateLocalDateTime: LocalDateTime): String {
        return dateLocalDateTime.toString()
    }

}