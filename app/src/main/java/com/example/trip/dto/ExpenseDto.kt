package com.example.trip.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ExpenseDto(
    val expenditureId: Long,
    val generationDate: LocalDate,
    val title: String,
    val price: BigDecimal,
    val groupId: Long,
    val creatorId: Long,
    val expenseDebtors: List<Long>
)