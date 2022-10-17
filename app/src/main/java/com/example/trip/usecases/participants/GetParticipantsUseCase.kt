package com.example.trip.usecases.participants

import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetParticipantsUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    operator fun invoke(groupId: Int): Flow<Resource<List<Participant>>> {
        return participantsRepository.getParticipantsForGroup(groupId)
    }

}