package com.example.trip.usecases.finances

import com.example.trip.models.CheckableParticipant
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.math.BigDecimal
import javax.inject.Inject

class GetCheckableParticipantsUseCase @Inject constructor(
    private val participantsRepository: ParticipantsRepository
) {

    suspend operator fun invoke(groupId: Long): Flow<Resource<List<CheckableParticipant>>> {
        return flow {
            emit(getParticipants(groupId))
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

    private suspend fun getParticipants(groupId: Long): Resource<List<CheckableParticipant>> {
        val participants = participantsRepository.getParticipantsForGroup(groupId)
        val result = participants.map {
            val participant = Participant(
                it.userId,
                "${it.firstName} ${it.surname}",
                it.email,
                it.phoneNumber,
                UserRole.UNSPECIFIED
            )
            CheckableParticipant(
                participant,
                BigDecimal.ZERO,
                false
            )
        }
        return Resource.Success(result)
    }

}