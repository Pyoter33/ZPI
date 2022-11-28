package com.example.trip.utils.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter {

    @FromJson
    fun decode(dateString: String?): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateString, formatter)
    }

    @ToJson
    fun encode(dateLocalDate: LocalDate?): String? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return dateLocalDate?.format(formatter)
    }

}