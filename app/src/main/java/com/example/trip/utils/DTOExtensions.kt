package com.example.trip.utils

import com.example.trip.dto.ExpenseDto
import com.example.trip.models.Expense

fun Expense.toExpenseDto(): ExpenseDto {
    return ExpenseDto(
        id,
        creationDate,
        title,
        price,
        groupId,
        creator.id,
        debtors.map { it.id }
    )
}
