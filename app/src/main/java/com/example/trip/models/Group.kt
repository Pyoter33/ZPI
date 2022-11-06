package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val id: Long,
    val name: String,
    val groupStatus: GroupStatus,
    val startCity: String,
    val currency: String,
    val minParticipants: Int,
    val minDays: Int,
    val description: String?,
    val participantsNo: Int
): Parcelable
