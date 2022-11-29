package com.example.trip.usecases.finances

import com.example.trip.models.CheckableParticipant
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.math.BigDecimal

import javax.inject.Inject

class GetCheckableParticipantsUseCase @Inject constructor(
    private val participantsRepository: ParticipantsRepository,
    private val groupsRepository: GroupsRepository
) {

    suspend operator fun invoke(groupId: Long): Flow<Resource<List<CheckableParticipant>>> {
        return try {
            flowOf(getParticipants(groupId))
        } catch (e: HttpException) {
            e.printStackTrace()
            flowOf(Resource.Failure())
        } catch (e: Exception) {
            e.printStackTrace()
            flowOf(Resource.Failure(0))
        }.onStart {
            emit(Resource.Loading())
        }
    }

    private suspend fun getParticipants(groupId: Long): Resource<List<CheckableParticipant>> {
        val participants = participantsRepository.getParticipantsForGroup(groupId)
        val coordinatorsIds = groupsRepository.getCoordinators(groupId).map { it.userId }
        val result = participants.map {
            val participant = Participant(
                it.userId,
                "${it.firstName} ${it.surname}",
                it.email,
                it.phoneNumber,
                if (it.userId in coordinatorsIds) UserRole.COORDINATOR else UserRole.BASIC_USER
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