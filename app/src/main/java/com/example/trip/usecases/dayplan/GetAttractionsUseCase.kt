package com.example.trip.usecases.dayplan

import com.example.trip.Constants
import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class GetAttractionsUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository, @Named(Constants.GOOGLE_API_KEY) private val apiKey: String?) {

    suspend operator fun invoke(dayPlanId: Long): Flow<Resource<List<Attraction>>> {
        return flow {
            emit(getAttractions(dayPlanId))
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getAttractions(dayPlanId: Long): Resource<List<Attraction>> =
        Resource.Success(dayPlansRepository.getAttractionsForDayPlan(dayPlanId).map {
            val attractionDto = it.attraction
            Attraction(
                attractionDto.attractionId,
                dayPlanId,
                attractionDto.name,
                attractionDto.address,
                attractionDto.description,
                getPhotoUrl(attractionDto.photoLink),
                attractionDto.photoLink,
                attractionDto.attractionLink,
                it.distanceToNextAttraction
            )
        }
        )

    private fun getPhotoUrl(reference: String?): String? {
        return reference?.let { "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photo_reference=$reference&key=$apiKey" }
    }

}
