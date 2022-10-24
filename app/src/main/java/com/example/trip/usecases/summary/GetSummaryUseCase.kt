package com.example.trip.usecases.summary

import com.example.trip.models.Resource
import com.example.trip.models.Summary
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val accommodationsRepository: AccommodationsRepository,
    private val availabilityRepository: AvailabilityRepository,
    private val participantsRepository: ParticipantsRepository
) {

    operator fun invoke(groupId: Int): Flow<Resource<Summary>> {
        return combine(
            accommodationsRepository.getAcceptedAccommodation(groupId),
            availabilityRepository.getAcceptedAvailability(groupId),
            participantsRepository.getParticipantsForGroup(groupId)
        ) { acceptedAccommodation, acceptedAvailability, participants ->
            if (acceptedAccommodation is Resource.Success && acceptedAvailability is Resource.Success && participants is Resource.Success) {
                Resource.Success(
                    Summary(
                        groupId,
                        acceptedAccommodation.data,
                        acceptedAvailability.data,
                        participants.data
                    )
                )
            } else {
                Resource.Failure()
            }
        }
    }


}