package com.example.trip.usecases.finances

import com.example.trip.models.Balance
import com.example.trip.models.BalanceStatus
import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val participantsRepository: ParticipantsRepository
) {
    operator fun invoke(groupId: Long): Flow<Resource<List<Balance>>> {
        val result = combine(
            financesRepository.getBalances(groupId),
            participantsRepository.getParticipantsForGroup(groupId)
        ) { balancesDto, participants ->
            if (balancesDto is Resource.Failure || participants is Resource.Failure) return@combine Resource.Failure<List<Balance>>()
            if (balancesDto is Resource.Loading || participants is Resource.Loading) return@combine Resource.Loading()

            val maxAmount = (balancesDto as Resource.Success).data.values.max()

            val balances = balancesDto.data.map { entry ->
                val participant= (participants as Resource.Success).data.find { it.id == entry.key } ?: return@combine Resource.Failure<List<Balance>>()
                if (entry.value.toDouble() < 0) {
                    Balance(
                        participant,
                        entry.value,
                        maxAmount,
                        BalanceStatus.NEGATIVE
                    )
                } else {
                    Balance(
                        participant,
                        entry.value,
                        maxAmount,
                        BalanceStatus.POSITIVE
                    )
                }
            }
            Resource.Success(balances)
        }
        return result
    }

}