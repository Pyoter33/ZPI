package com.example.trip.models

import java.math.BigDecimal

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
    var isExpanded: Boolean = false
)