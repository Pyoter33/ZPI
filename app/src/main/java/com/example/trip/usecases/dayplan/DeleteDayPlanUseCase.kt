package com.example.trip.usecases.dayplan

import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class DeleteDayPlanUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(dayPlanId: Long): Resource<Unit> {
        return try {
            dayPlansRepository.deleteDayPlan(dayPlanId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: ConnectException) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}