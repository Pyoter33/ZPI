package com.example.trip.usecases.summary

import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAcceptedAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {
    operator fun invoke(groupId: Int): Flow<Resource<Availability?>> {
        return availabilityRepository.getAcceptedAvailability(groupId)
    }

}