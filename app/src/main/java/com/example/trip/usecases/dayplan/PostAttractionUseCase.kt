package com.example.trip.usecases.dayplan

import com.example.trip.dto.AttractionCandidateDto
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class PostAttractionUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    suspend operator fun invoke(dayPlanId: Long, attractionCandidateDto: AttractionCandidateDto): Resource<Unit> {
        return try {
            dayPlansRepository.postAttraction(dayPlanId, attractionCandidateDto)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure()
        } catch (e: ConnectException) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}