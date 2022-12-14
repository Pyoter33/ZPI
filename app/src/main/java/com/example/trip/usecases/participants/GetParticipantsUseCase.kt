package com.example.trip.usecases.participants

import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.utils.getMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetParticipantsUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository, private val groupsRepository: GroupsRepository) {

    operator fun invoke(groupId: Long): Flow<Resource<List<Participant>>> {
        return flow {
            emit(getParticipants(groupId))
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

    private suspend fun getParticipants(groupId: Long): Resource<List<Participant>> {
        val coordinatorsIds =  groupsRepository.getCoordinators(groupId).map { it.userId }
        val result = participantsRepository.getParticipantsForGroup(groupId).map {
            Participant(
                it.userId,
                "${it.firstName} ${it.surname}",
                it.email,
                it.phoneNumber,
                if (it.userId in coordinatorsIds) UserRole.COORDINATOR else UserRole.BASIC_USER
            )
        }
        return Resource.Success(result)
    }

}