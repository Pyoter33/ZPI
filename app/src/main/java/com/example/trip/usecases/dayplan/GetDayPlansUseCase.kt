package com.example.trip.usecases.dayplan

import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDayPlansUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    operator fun invoke(groupId: Int): Flow<Resource<List<DayPlan>>> {
        return dayPlansRepository.getDayPlans(groupId)
    }

}