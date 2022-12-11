package com.example.trip.usecases.finances

import com.example.trip.models.Balance
import com.example.trip.models.BalanceStatus
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.utils.getMessage
import com.example.trip.utils.toParticipant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.math.BigDecimal
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
            if (it is HttpException) {
                emit(Resource.Failure(it.code(), it.response()?.getMessage()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getBalances(groupId: Long): Resource<List<Balance>> {
        val balancesDto = financesRepository.getBalances(groupId)
        val participants = participantsRepository.getParticipantsForGroup(groupId)
        val maxAmount = balancesDto.values.maxOrNull() ?: BigDecimal.ONE
        val minAmount = balancesDto.values.minOrNull() ?: BigDecimal.ONE
        val maxIndicator = maxAmount.max(minAmount.abs())

        val balances = participants.map { participant ->
            val balance = balancesDto[participant.userId]
            balance?.let {
                if (it.toDouble() < 0) {
                    Balance(
                        participant.toParticipant(UserRole.UNSPECIFIED),
                        it,
                        maxIndicator,
                        BalanceStatus.NEGATIVE
                    )
                } else {
                    Balance(
                        participant.toParticipant(UserRole.UNSPECIFIED),
                        it,
                        maxIndicator,
                        BalanceStatus.POSITIVE
                    )
                }
            } ?: Balance(
                participant.toParticipant(UserRole.UNSPECIFIED),
                BigDecimal.ZERO,
                maxIndicator,
                BalanceStatus.NEUTRAL
            )
        }
        return Resource.Success(balances)
    }

}