package com.example.trip.usecases.dayplan

import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import retrofit2.HttpException

import javax.inject.Inject

class DeleteAttractionUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(id: Long, dayPlanId: Long): Resource<Unit> {
        return try {
            dayPlansRepository.deleteAttraction(id, dayPlanId)
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