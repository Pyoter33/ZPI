package com.example.trip.usecases.finances

import com.example.trip.models.Resource
import com.example.trip.models.Settlement
import com.example.trip.models.UserRole
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.utils.SharedPreferencesHelper
import com.example.trip.utils.toParticipant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetSettlementsUseCase @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val participantsRepository: ParticipantsRepository,
    private val preferencesHelper: SharedPreferencesHelper
) {

    operator fun invoke(groupId: Long): Flow<Resource<List<Settlement>>> {
        return flow {
            emit(getSettlements(groupId))
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

    private suspend fun getSettlements(groupId: Long): Resource<List<Settlement>> {
        val settlementsDtos = financesRepository.getSettlements(groupId, preferencesHelper.getUserId())
        val participants = participantsRepository.getParticipantsForGroup(groupId)
        val settlements = settlementsDtos.map { settlementDto ->
            val participantDebtor =
                participants.find { it.userId == settlementDto.debtor } ?: return Resource.Failure()
            val participantDebtee =
                participants.find { it.userId == settlementDto.debtee } ?: return Resource.Failure()
            Settlement(
                settlementDto.financialRequestId,
                settlementDto.groupId,
                settlementDto.status,
                settlementDto.amount,
                participantDebtor.toParticipant(UserRole.UNSPECIFIED),
                participantDebtee.toParticipant(UserRole.UNSPECIFIED)
            )
        }
        return Resource.Success(settlements)
    }

}