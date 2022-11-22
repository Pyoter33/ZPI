package com.example.trip.usecases.availability

import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOptimalDatesUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    operator fun invoke(groupId: Long): Flow<Resource<List<OptimalAvailability>>> =
        availabilityRepository.getOptimalDates(groupId)

}