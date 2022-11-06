package com.example.trip.models

import java.math.BigDecimal
import java.time.LocalDate

data class Expense(
    val id: Long,
    val groupId: Long,
    val creatorId: Long,
    val creationDate: LocalDate,
    val title: String,
    val price: BigDecimal,
    val debtors: List<Participant>
)