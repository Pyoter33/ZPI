package com.example.trip.repositories

import com.example.trip.dto.ExpenseDto
import com.example.trip.dto.ExpensePostDto
import com.example.trip.dto.SettlementDto
import com.example.trip.service.FinanceService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import java.math.BigDecimal
import javax.inject.Inject

class FinancesRepository @Inject constructor(private val financeService: FinanceService) {

    suspend fun getExpenses(groupId: Long): List<ExpenseDto> {
        return financeService.getExpenses(groupId).toBodyOrError()
    }

    suspend fun getSettlements(groupId: Long, userId: Long): List<SettlementDto> {
        return financeService.getSettlements(groupId, userId).toBodyOrError()
    }

    suspend fun getBalances(groupId: Long): Map<Long, BigDecimal> {
        return financeService.getBalances(groupId).toBodyOrError()
    }

    suspend fun postExpense(groupId: Long, expensePostDto: ExpensePostDto) {
        financeService.postExpense(groupId, expensePostDto).toNullableBodyOrError()
    }

    suspend fun updateExpense(groupId: Long, expenseId: Long, expensePostDto: ExpensePostDto) {
        financeService.updateExpense(groupId, expenseId, expensePostDto).toNullableBodyOrError()
    }

    suspend fun deleteExpense(expenseId: Long, groupId: Long) {
        financeService.deleteExpense(expenseId, groupId).toNullableBodyOrError()
    }

    suspend fun resolveSettlement(id: Long, groupId: Long) {
        financeService.resolveSettlement(id, groupId).toNullableBodyOrError()
    }

}