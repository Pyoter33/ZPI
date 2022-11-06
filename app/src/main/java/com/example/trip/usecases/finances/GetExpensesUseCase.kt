package com.example.trip.usecases.finances

import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val participantsRepository: ParticipantsRepository
) {

    operator fun invoke(groupId: Long): Flow<Resource<List<Expense>>> {
        val result = combine(
            financesRepository.getExpenses(groupId),
            participantsRepository.getParticipantsForGroup(groupId)
        ) { expensesDtos, participants ->
            if (expensesDtos is Resource.Failure || participants is Resource.Failure) return@combine Resource.Failure<List<Expense>>()
            if (expensesDtos is Resource.Loading || participants is Resource.Loading) return@combine Resource.Loading()

             val expenses =  (expensesDtos as Resource.Success).data.map { expenseDto ->
                val participantsDebtors = expenseDto.expenseDebtors.map { debtorId ->
                    val participant = (participants as Resource.Success).data.find { it.id == debtorId }
                        ?: return@combine Resource.Failure<List<Expense>>()
                    participant
                }
                    Expense(
                        expenseDto.expenditureId,
                        expenseDto.groupId,
                        expenseDto.creatorId,
                        expenseDto.generationDate,
                        expenseDto.title,
                        expenseDto.price,
                        participantsDebtors
                    )
            }
            Resource.Success(expenses)
        }
        return result
    }

}