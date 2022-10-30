package com.example.trip.usecases.availability

import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import javax.inject.Inject

class DeleteAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(id: Long, groupId: Int): Resource<Unit> {
        return availabilityRepository.deleteAvailability(id)
    }
}