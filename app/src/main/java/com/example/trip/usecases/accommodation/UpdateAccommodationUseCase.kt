package com.example.trip.usecases.accommodation

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import javax.inject.Inject

class UpdateAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodation: Accommodation): Resource<Unit> {
        return accommodationsRepository.updateAccommodation(accommodation)
    }

}