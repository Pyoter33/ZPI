package com.example.trip.usecases.dayplan

import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import javax.inject.Inject

class PostDayPlanUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(dayPlan: DayPlan): Resource<Unit> {
        return dayPlansRepository.postDayPlan(dayPlan)
    }

}