package com.example.trip.usecases.finances

import com.example.trip.models.CheckableParticipant
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class GetCheckableParticipantsUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    operator fun invoke(groupId: Long): Flow<Resource<List<CheckableParticipant>>> {
        return participantsRepository.getParticipantsForGroup(groupId).map {
            if (it is Resource.Failure) return@map Resource.Failure<List<CheckableParticipant>>()
            if (it is Resource.Loading) return@map Resource.Loading()

            val participants = (it as Resource.Success).data.map { participant ->
                CheckableParticipant(
                    participant,
                    BigDecimal.ZERO,
                    false
                )
            }
            Resource.Success(participants)
        }

    }

}