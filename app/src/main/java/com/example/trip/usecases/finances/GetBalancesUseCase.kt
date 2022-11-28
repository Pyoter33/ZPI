package com.example.trip.usecases.finances

import com.example.trip.models.Balance
import com.example.trip.models.BalanceStatus
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

class GetBalancesUseCase @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val participantsRepository: ParticipantsRepository
) {
    suspend operator fun invoke(groupId: Long): Flow<Resource<List<Balance>>> {
        return flow {
            emit(getBalances(groupId))
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

    private suspend fun getBalances(groupId: Long): Resource<List<Balance>> {
        val balancesDto = financesRepository.getBalances(groupId)
        val participants = participantsRepository.getParticipantsForGroup(groupId)
        val maxAmount = balancesDto.values.maxOrNull()

        val balances = balancesDto.map { entry ->
            val participant= participants.find { it.userId == entry.key } ?: return Resource.Failure()
            if (entry.value.toDouble() < 0) {
                Balance(
                    participant.toParticipant(UserRole.UNSPECIFIED),
                    entry.value,
                    maxAmount!!,
                    BalanceStatus.NEGATIVE
                )
            } else {
                Balance(
                    participant.toParticipant(UserRole.UNSPECIFIED),
                    entry.value,
                    maxAmount!!,
                    BalanceStatus.POSITIVE
                )
            }
        }
        return Resource.Success(balances)
    }

}