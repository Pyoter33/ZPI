package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attraction(
    val id: Int,
    val groupId: Int,
    val dayPlanId: Int,
    val name: String,
    val address: String,
    var description: String,
    val imageUrl: String,
    val link: String,
    val distanceToNext: Double?,
    var isExpanded: Boolean = false
) : Parcelable