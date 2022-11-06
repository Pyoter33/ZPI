package com.example.trip.repositories


import com.example.trip.dto.ExpenseDto
import com.example.trip.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.time.LocalDate
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
                            BigDecimal.valueOf(170.4),
                            0,
                            1,
                            listOf(1, 2, 3, 4)
                        ),
                        ExpenseDto(
                            0,
                            LocalDate.of(2022, 10, 11),
                            "Fruit",
                            BigDecimal.valueOf(30.1),
                            0,
                            1,
                            listOf(1, 2, 3, 4)
                        ), ExpenseDto(
                            0,
                            LocalDate.of(2022, 10, 12),
                            "Souvenirs",
                            BigDecimal.valueOf(16),
                            0,
                            2,
                            listOf(1, 2)
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


}