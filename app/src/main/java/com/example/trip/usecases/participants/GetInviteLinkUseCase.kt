package com.example.trip.usecases.participants

import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInviteLinkUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    operator fun invoke(groupId: Int): Flow<Resource<String>> {
        return participantsRepository.getInviteLink(groupId)
    }

}