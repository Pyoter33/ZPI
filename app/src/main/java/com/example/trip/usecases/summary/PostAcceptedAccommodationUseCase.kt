package com.example.trip.usecases.summary

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import javax.inject.Inject

class PostAcceptedAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodation: Accommodation): Resource<Unit> {
        return accommodationsRepository.postAcceptedAccommodation(accommodation)
    }

}