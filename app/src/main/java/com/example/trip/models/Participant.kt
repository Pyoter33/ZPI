package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participant(
    val id: Long,
    val groupId: Long,
    val fullName: String,
    val email: String,
    val role: UserRole
): Parcelable