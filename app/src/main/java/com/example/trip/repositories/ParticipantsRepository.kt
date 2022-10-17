package com.example.trip.repositories

import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ParticipantsRepository @Inject constructor() {

    fun getParticipantsForGroup(groupId: Int): Flow<Resource<List<Participant>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Participant(1, 0, "Dorian Olisadebe", "participant@example.com", UserRole.BASIC_USER),
                        Participant(2, 0, "Krzysztof Siedem", "participant@example.com", UserRole.BASIC_USER),
                        Participant(3, 0, "Kajetan Boba", "participant@example.com", UserRole.COORDINATOR),
                        Participant(4, 0, "Piotr Marian", "participant@example.com", UserRole.BASIC_USER),
                        Participant(5, 0, "Participant 1", "participant@example.com", UserRole.BASIC_USER),
                        Participant(6, 0, "Participant 2", "participant@example.com", UserRole.BASIC_USER),
                    )
                )
            )
        }
    }

    suspend fun updateParticipant(participant: Participant): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun deleteParticipant(groupId: Int, id: Int): Resource<Unit> {
        return Resource.Failure()
    }

}