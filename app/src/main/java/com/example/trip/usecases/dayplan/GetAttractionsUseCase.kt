package com.example.trip.usecases.dayplan

import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttractionsUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    operator fun invoke(groupId: Int, dayPlanId: Int): Flow<Resource<List<Attraction>>> {
        return dayPlansRepository.getAttractionsForDayPlan(groupId, dayPlanId)
    }

}