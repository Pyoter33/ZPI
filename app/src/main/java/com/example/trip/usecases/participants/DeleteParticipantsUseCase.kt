package com.example.trip.usecases.participants

import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import javax.inject.Inject

class DeleteParticipantsUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    suspend operator fun invoke(groupId: Int, id: Int): Resource<Unit> {
        return participantsRepository.deleteParticipant(groupId, id)
    }

}