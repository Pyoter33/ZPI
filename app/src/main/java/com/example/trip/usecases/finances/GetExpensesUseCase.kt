package com.example.trip.usecases.finances

import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.utils.toParticipant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val participantsRepository: ParticipantsRepository
) {

    operator fun invoke(groupId: Long): Flow<Resource<List<Expense>>> {
        return flow {
            emit(getExpenses(groupId))
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

    private suspend fun getExpenses(groupId: Long): Resource<List<Expense>> {
        val expensesDtos = financesRepository.getExpenses(groupId)
        val participants = participantsRepository.getParticipantsForGroup(groupId)
        val expenses = expensesDtos.map { expenseDto ->
            val participantsDebtors = expenseDto.expenseDebtors.map { debtorId ->
                val participant = participants.find { it.userId == debtorId }
                    ?: return Resource.Failure()
                participant.toParticipant(UserRole.UNSPECIFIED)
            }
            val participantCreator = participants.find { it.userId == expenseDto.creatorId }
                ?: return Resource.Failure()
            Expense(
                expenseDto.expenditureId,
                expenseDto.groupId,
                participantCreator.toParticipant(UserRole.UNSPECIFIED),
                expenseDto.generationDate.toLocalDate(),
                expenseDto.title,
                expenseDto.price,
                participantsDebtors
            )
        }
        return Resource.Success(expenses)
    }

}