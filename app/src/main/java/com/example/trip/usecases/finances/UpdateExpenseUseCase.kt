package com.example.trip.usecases.finances

import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import com.example.trip.utils.toExpenseDto
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(private val financesRepository: FinancesRepository) {
    suspend operator fun invoke(expense: Expense): Resource<Unit> {
        return financesRepository.updateExpense(expense.toExpenseDto())
    }
}