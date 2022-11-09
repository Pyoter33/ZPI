package com.example.trip.usecases.finances

import com.example.trip.models.Resource
import com.example.trip.models.Settlement
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSettlementsUseCase @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val participantsRepository: ParticipantsRepository
) {

    operator fun invoke(groupId: Long): Flow<Resource<List<Settlement>>> {
        val result = combine(
            financesRepository.getSettlements(groupId),
            participantsRepository.getParticipantsForGroup(groupId)
        ) { settlementsDtos, participants ->
            if (settlementsDtos is Resource.Failure || participants is Resource.Failure) return@combine Resource.Failure<List<Settlement>>()
            if (settlementsDtos is Resource.Loading || participants is Resource.Loading) return@combine Resource.Loading()

            val settlements = (settlementsDtos as Resource.Success).data.map { expenseDto ->
                val participantDebtor = (participants as Resource.Success).data.find { it.id == expenseDto.debtor } ?: return@combine Resource.Failure<List<Settlement>>()
                val participantDebtee = participants.data.find { it.id == expenseDto.debtee } ?: return@combine Resource.Failure<List<Settlement>>()
                Settlement(
                    expenseDto.financialRequestId,
                    expenseDto.groupId,
                    expenseDto.status,
                    expenseDto.amount,
                    participantDebtor,
                    participantDebtee
                )
            }
            Resource.Success(settlements)
        }
        return result
    }

}