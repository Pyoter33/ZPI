package com.example.trip.usecases.availability

import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import javax.inject.Inject

class PostAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(availability: Availability): Resource<Unit> {
        return availabilityRepository.postAvailability(availability)
    }

}