package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class DayPlan(
    val id: Long,
    val groupId: Long,
    val name: String,
    val date: LocalDate,
    val attractionsNumber: Int,
    val iconCode: Int
): Parcelable
