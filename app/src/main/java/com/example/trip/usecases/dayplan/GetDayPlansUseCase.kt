package com.example.trip.usecases.dayplan

import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetDayPlansUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(groupId: Long): Flow<Resource<List<DayPlan>>> {
        return flow {
            emit(getDayPlans(groupId))
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

    private suspend fun getDayPlans(groupId: Long): Resource<List<DayPlan>> =
        Resource.Success(dayPlansRepository.getDayPlans(groupId).map {
            DayPlan(
                it.dayPlanId,
                it.groupId,
                it.name,
                it.date,
                it.dayAttractions.size,
                it.iconType
            )
        }
        )

}