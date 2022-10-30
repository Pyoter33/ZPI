package com.example.trip.usecases.availability

import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAvailabilitiesUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    operator fun invoke(userId: Long, groupId: Long): Flow<Resource<List<Availability>>> =
        availabilityRepository.getUserAvailabilities(userId, groupId)

}