package com.example.trip.usecases.summary

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAcceptedAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {
    operator fun invoke(groupId: Long): Flow<Resource<Accommodation?>> {
        return accommodationsRepository.getAcceptedAccommodation(groupId)
    }

}