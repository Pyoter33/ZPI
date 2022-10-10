package com.example.trip.usecases.accommodation

import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import javax.inject.Inject

class PostAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(groupId: Int, accommodationBase: Pair<String, String?>): Resource<Unit> {
        return accommodationsRepository.postAccommodation(groupId, accommodationBase)
    }

}