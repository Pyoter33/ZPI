package com.example.trip.usecases.participants

import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import javax.inject.Inject

class UpdateParticipantUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    suspend operator fun invoke(participant: Participant): Resource<Unit> {
        return Resource.Failure()
    }

}