package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Accommodation(
    val id: Long,
    val groupId: Long,
    val creatorId: Long,
    val name: String,
    val address: String,
    val description: String?,
    val imageUrl: String,
    val sourceUrl: String,
    var votes: Int,
    val price: BigDecimal,
    var isVoted: Boolean,
    val isAccepted: Boolean = false,
    var isExpanded: Boolean = false
): Parcelable