package com.example.trip.usecases.dayplan

import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.utils.getMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetDayPlansUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(groupId: Long): Flow<Resource<List<DayPlan>>> {
        return flow {
            emit(getDayPlans(groupId))
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code(), it.response()?.getMessage()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
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