package com.example.trip.usecases.accommodation

import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import javax.inject.Inject

class PostAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodationBase: Pair<String, String?>): Resource<Unit> {
        return accommodationsRepository.postAccommodation(accommodationBase)
    }

}