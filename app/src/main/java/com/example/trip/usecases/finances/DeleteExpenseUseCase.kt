package com.example.trip.usecases.finances

import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(private val financesRepository: FinancesRepository) {
    suspend operator fun invoke(id: Long, groupId: Long): Resource<Unit> {
        return financesRepository.deleteExpense(id, groupId)
    }

}