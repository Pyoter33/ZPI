package com.example.trip.usecases.accommodation

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccommodationsListUseCase @Inject constructor(private val repository: AccommodationsRepository) {

    operator fun invoke(groupId: Int): Flow<Resource<List<Accommodation>>> {
        return repository.getAccommodationsList(groupId)
    }
}