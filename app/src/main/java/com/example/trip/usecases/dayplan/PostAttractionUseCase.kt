package com.example.trip.usecases.dayplan

import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import javax.inject.Inject

class PostAttractionUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(attraction: Attraction): Resource<Unit> {
        return dayPlansRepository.postAttraction(attraction)
    }

}