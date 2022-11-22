package com.example.trip.service

import com.example.trip.dto.ExpenseDto
import com.example.trip.dto.ExpensePostDto
import com.example.trip.dto.SettlementDto
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal

interface FinanceService {

    @GET("finance-optimizer")
    suspend fun getExpenses(
        @Query("groupId") groupId: Long
    ): Response<List<ExpenseDto>>

    @POST("finance-optimizer")
    suspend fun postExpense(
        @Query("groupId") groupId: Long,
        @Body expensePostDto: ExpensePostDto
    ): Response<Void>

    @PATCH("finance-optimizer")
    suspend fun updateExpense(
        @Query("groupId") groupId: Long,
        @Query("expenditureId") expenditureId: Long,
        @Body expensePostDto: ExpensePostDto
    ): Response<Void>

    @DELETE("finance-optimizer")
    suspend fun deleteExpense(
        @Query("expenditureId") expenditureId: Long,
        @Query("groupId") groupId: Long
    ): Response<Void>

    @GET("finance-optimizer/balance")
    suspend fun getBalances(
        @Query("groupId") groupId: Long
    ): Response<Map<Long, BigDecimal>>

    @GET("finance-request")
    suspend fun getSettlements(
        @Query("groupId") groupId: Long
    ): Response<List<SettlementDto>>

    @GET("finance-request/accept")
    suspend fun acceptSettlement(
        @Query("requestId") requestId: Long,
        @Query("groupId") groupId: Long
    ): Response<Void>

}