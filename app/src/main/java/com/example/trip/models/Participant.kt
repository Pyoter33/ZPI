package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participant(
    val id: Long,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole
): Parcelable