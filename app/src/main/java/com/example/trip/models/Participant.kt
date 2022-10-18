package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participant(
    val id: Int,
    val groupId: Int,
    val fullName: String,
    val email: String,
    val role: UserRole
): Parcelable