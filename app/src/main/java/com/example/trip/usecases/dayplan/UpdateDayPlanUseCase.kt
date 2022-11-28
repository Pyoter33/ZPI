package com.example.trip.usecases.dayplan

import com.example.trip.dto.DayPlanPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class UpdateDayPlanUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(dayPlanId: Long, dayPlanPostDto: DayPlanPostDto): Resource<Unit> {
        return try {
            dayPlansRepository.updateDayPlan(dayPlanId, dayPlanPostDto)
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