package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttractionPreview(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String,
    val imageUrl: String?,
    val imageReference: String?,
    val link: String
): Parcelable
