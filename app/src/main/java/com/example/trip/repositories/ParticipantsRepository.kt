package com.example.trip.repositories

import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ParticipantsRepository @Inject constructor() {

    fun getInviteLink(groupId: Long): Flow<Resource<String>> {
        return flow {
            emit(
                Resource.Success(
                    "https://travelnow/invite?token=aegewg3425yhg30gh4rahrah4563yhea-3gh"
                )
            )
        }
    }

    fun getParticipantsForGroup(groupId: Long): Flow<Resource<List<Participant>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Participant(3, 0, "Kajetan Boba", "bo-ba7312@onet.pl", UserRole.COORDINATOR),
                        Participant(4, 0, "Piotr Marian", "piterm44@gmail.com", UserRole.COORDINATOR),
                        Participant(1, 0, "Dorian Olisadebe", "olisadebe@gmail.com", UserRole.BASIC_USER),
                        Participant(2, 0, "Krzysztof Siedem", "krzysztof77@gmail.com", UserRole.BASIC_USER)
                    )
                )
            )
        }
    }

    suspend fun updateParticipant(participant: Participant): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun deleteParticipant(id: Long, groupId: Long): Resource<Unit> {
        return Resource.Failure()
    }

}