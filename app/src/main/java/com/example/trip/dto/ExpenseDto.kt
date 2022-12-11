package com.example.trip.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ExpenseDto(
    val expenditureId: Long,
    val generationDate: LocalDateTime,
    val title: String,
    val price: BigDecimal,
    val groupId: Long,
    val creatorId: Long,
    val expenseDebtors: List<Long>
)