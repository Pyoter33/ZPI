package com.example.trip.usecases.dayplan

import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import retrofit2.HttpException
import javax.inject.Inject

class UpdateStartingPointUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(dayPlanId: Long, attractionId: Long): Resource<Unit> {
        return try {
            dayPlansRepository.updateStartingPoint(dayPlanId, attractionId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}