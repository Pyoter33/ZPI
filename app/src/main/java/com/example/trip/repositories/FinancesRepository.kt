package com.example.trip.repositories


import com.example.trip.dto.ExpenseDto
import com.example.trip.dto.SettlementDto
import com.example.trip.models.Resource
import com.example.trip.models.SettlementStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class FinancesRepository @Inject constructor() {

    fun getExpenses(groupId: Long): Flow<Resource<List<ExpenseDto>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        ExpenseDto(
                            0,
                            LocalDate.of(2022, 10, 11),
                            "Dinner",
                            BigDecimal.valueOf(170.40),
                            0,
                            1,
                            listOf(1, 2, 3, 4)
                        ),
                        ExpenseDto(
                            0,
                            LocalDate.of(2022, 10, 11),
                            "Fruit",
                            BigDecimal.valueOf(30.10),
                            0,
                            1,
                            listOf(2, 3, 4)
                        ), ExpenseDto(
                            0,
                            LocalDate.of(2022, 10, 12),
                            "Souvenirs",
                            BigDecimal.valueOf(16),
                            0,
                            2,
                            listOf(2)
                        ), ExpenseDto(
                            0,
                            LocalDate.of(2022, 10, 13),
                            "Bus ticket",
                            BigDecimal.valueOf(12),
                            0,
                            3,
                            listOf(1, 4)
                        )
                    )
                )
            )
        }
    }

    fun getSettlements(groupId: Long): Flow<Resource<List<SettlementDto>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        SettlementDto(
                            0,
                            LocalDateTime.of(2022, 10, 11, 0, 0),
                            SettlementStatus.PENDING,
                            BigDecimal.valueOf(14.4),
                            2,
                            1,
                            0
                        ),
                        SettlementDto(
                            0,
                            LocalDateTime.of(2022, 10, 11, 0, 0),
                            SettlementStatus.PENDING,
                            BigDecimal.valueOf(10.4),
                            3,
                            4,
                            0
                        ),
                        SettlementDto(
                            0,
                            LocalDateTime.of(2022, 10, 11, 0, 0),
                            SettlementStatus.RESOLVED,
                            BigDecimal.valueOf(30.4),
                            3,
                            1,
                            0
                        ),
                        SettlementDto(
                            0,
                            LocalDateTime.of(2022, 10, 11, 0, 0),
                            SettlementStatus.PENDING,
                            BigDecimal.valueOf(11.4),
                            2,
                            4,
                            0
                        )
                    )
                )
            )
        }
    }


}