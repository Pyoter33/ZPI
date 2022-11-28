package com.example.trip.dto

data class ExpensePostDto(
    val creatorId: Long,
    val title: String,
    val price: Double,
    val debtorsIds: List<Long>
)